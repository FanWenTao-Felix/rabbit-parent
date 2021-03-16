package ink.ckx.rabbit.producer.service;

import ink.ckx.rabbit.producer.constant.BrokerMessageStatus;
import ink.ckx.rabbit.producer.entity.BrokerMessage;
import ink.ckx.rabbit.producer.mapper.BrokerMessageMapper;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @auth chenkaixin
 * @date 2021/03/16
 */
@Service
public class MessageStoreService {

    private final BrokerMessageMapper brokerMessageMapper;

    public MessageStoreService(BrokerMessageMapper brokerMessageMapper) {
        this.brokerMessageMapper = brokerMessageMapper;
    }

    public int insert(BrokerMessage brokerMessage) {
        return this.brokerMessageMapper.insert(brokerMessage);
    }

    public BrokerMessage selectByMessageId(String messageId) {
        return this.brokerMessageMapper.selectByPrimaryKey(messageId);
    }

    public void success(String messageId) {
        this.brokerMessageMapper.changeBrokerMessageStatus(messageId, BrokerMessageStatus.SEND_OK.getCode(), new Date());
    }

    public void failure(String messageId) {
        this.brokerMessageMapper.changeBrokerMessageStatus(messageId,
                BrokerMessageStatus.SEND_FAIL.getCode(),
                new Date());
    }

    public List<BrokerMessage> fetchTimeOutMessage4Retry(BrokerMessageStatus brokerMessageStatus) {
        return this.brokerMessageMapper.queryBrokerMessageStatus4Timeout(brokerMessageStatus.getCode());
    }

    public int updateTryCount(String brokerMessageId) {
        return this.brokerMessageMapper.update4TryCount(brokerMessageId, new Date());
    }
}