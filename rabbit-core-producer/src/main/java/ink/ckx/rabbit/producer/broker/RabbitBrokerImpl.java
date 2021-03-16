package ink.ckx.rabbit.producer.broker;

import ink.ckx.rabbit.api.Message;
import ink.ckx.rabbit.api.MessageType;
import ink.ckx.rabbit.producer.constant.BrokerMessageConst;
import ink.ckx.rabbit.producer.constant.BrokerMessageStatus;
import ink.ckx.rabbit.producer.entity.BrokerMessage;
import ink.ckx.rabbit.producer.service.MessageStoreService;
import ink.ckx.rabbit.producer.util.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 发送消息的核心方法，使用异步线程池进行发送消息
 *
 * @auth chenkaixin
 * @date 2021/03/16
 */
@Slf4j
@Component
public class RabbitBrokerImpl implements RabbitBroker {

    private final RabbitTemplateContainer rabbitTemplateContainer;

    private final MessageStoreService messageStoreService;

    public RabbitBrokerImpl(RabbitTemplateContainer rabbitTemplateContainer, MessageStoreService messageStoreService) {
        this.rabbitTemplateContainer = rabbitTemplateContainer;
        this.messageStoreService = messageStoreService;
    }

    @Override
    public void reliantSend(Message message) {

        message.setMessageType(MessageType.RELIANT);
        BrokerMessage bm = messageStoreService.selectByMessageId(message.getMessageId());
        if (bm == null) {
            // 把数据库的消息发送日志先记录好
            Date now = new Date();
            BrokerMessage brokerMessage = new BrokerMessage();
            brokerMessage.setMessageId(message.getMessageId());
            brokerMessage.setStatus(BrokerMessageStatus.SENDING.getCode());
            // tryCount 在最开始发送的时候不需要进行设置
            brokerMessage.setNextRetryTime(DateUtils.addMinutes(now, BrokerMessageConst.TIMEOUT));
            brokerMessage.setCreateTime(now);
            brokerMessage.setUpdateTime(now);

            brokerMessage.setMessage(JSONUtil.toJson(message));
            messageStoreService.insert(brokerMessage);
        }
        // 执行真正的发送消息逻辑
        sendKernel(message);
    }

    @Override
    public void rapidSend(Message message) {

        message.setMessageType(MessageType.RAPID);
        sendKernel(message);
    }

    @Override
    public void confirmSend(Message message) {

        message.setMessageType(MessageType.CONFIRM);
        sendKernel(message);
    }

    @Override
    public void sendMessages() {

        List<Message> messages = MessageHolder.clear();
        messages.forEach(message ->
                MessageHolderAsyncQueue.submit(() -> {
                    this.convertAndSend(message);
                    log.info("#RabbitBrokerImpl.sendMessages# send to rabbitmq, messageId: {}", message.getMessageId());
                }));
    }

    /**
     * 发送消息的核心方法 使用异步线程池进行发送消息
     *
     * @param message
     */
    private void sendKernel(Message message) {

        AsyncBaseQueue.submit(() -> {
            this.convertAndSend(message);
            log.info("#RabbitBrokerImpl.sendKernel# send to rabbitmq, messageId: {}", message.getMessageId());
        });
    }

    private void convertAndSend(Message message) {

        CorrelationData correlationData = new CorrelationData(String.format("%s#%s#%s",
                message.getMessageId(),
                System.currentTimeMillis(),
                message.getMessageType()));
        String topic = message.getTopic();
        String routingKey = message.getRoutingKey();
        RabbitTemplate rabbitTemplate = rabbitTemplateContainer.getTemplate(message);
        rabbitTemplate.convertAndSend(topic, routingKey, message, correlationData);
    }
}