package ink.ckx.rabbit.producer.task;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.dataflow.DataflowJob;
import ink.ckx.rabbit.api.Message;
import ink.ckx.rabbit.producer.broker.RabbitBroker;
import ink.ckx.rabbit.producer.constant.BrokerMessageStatus;
import ink.ckx.rabbit.producer.entity.BrokerMessage;
import ink.ckx.rabbit.producer.service.MessageStoreService;
import ink.ckx.rabbit.producer.util.JSONUtil;
import ink.ckx.rabbit.task.annotation.ElasticJobConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author chenkaixin
 * @date 2021/03/16
 */
@Component
@ElasticJobConfig(
        name = "ink.ckx.rabbit.producer.task.RetryMessageDataflowJob",
        cron = "0/10 * * * * ?",
        description = "可靠性投递消息补偿任务",
        overwrite = true,
        shardingTotalCount = 1
)
@Slf4j
public class RetryMessageDataflowJob implements DataflowJob<BrokerMessage> {

    private static final int MAX_RETRY_COUNT = 3;
    private final MessageStoreService messageStoreService;
    private final RabbitBroker rabbitBroker;

    public RetryMessageDataflowJob(MessageStoreService messageStoreService, RabbitBroker rabbitBroker) {
        this.messageStoreService = messageStoreService;
        this.rabbitBroker = rabbitBroker;
    }

    @Override
    public List<BrokerMessage> fetchData(ShardingContext shardingContext) {

        List<BrokerMessage> list = messageStoreService.fetchTimeOutMessage4Retry(BrokerMessageStatus.SENDING);
        log.info("-------- 抓取数据集合，数量 : {} -----------", list.size());
        return list;
    }

    @Override
    public void processData(ShardingContext shardingContext, List<BrokerMessage> dataList) {

        dataList.forEach(brokerMessage -> {

            String messageId = brokerMessage.getMessageId();
            if (brokerMessage.getTryCount() >= MAX_RETRY_COUNT) {
                this.messageStoreService.failure(messageId);
                log.warn("-------- 消息设置为最终失败，消息id : {} --------", messageId);
            } else {
                //	每次重发的时候要更新一下  try count 字段
                this.messageStoreService.updateTryCount(messageId);
                // 	重发消息
                this.rabbitBroker.reliantSend(JSONUtil.fromJson(brokerMessage.getMessage(), Message.class));
            }
        });
    }
}