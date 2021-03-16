package ink.ckx.rabbit.producer.broker;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import ink.ckx.rabbit.api.Message;
import ink.ckx.rabbit.api.MessageType;
import ink.ckx.rabbit.api.exception.MessageRunTimeException;
import ink.ckx.rabbit.common.convert.GenericMessageConverter;
import ink.ckx.rabbit.common.convert.RabbitMessageConverter;
import ink.ckx.rabbit.common.serializer.Serializer;
import ink.ckx.rabbit.common.serializer.SerializerFactory;
import ink.ckx.rabbit.common.serializer.impl.JacksonSerializerFactory;
import ink.ckx.rabbit.producer.service.MessageStoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 每一个 topic 对应一个 RabbitTemplate
 * 1. 提高发送的效率
 * 2. 可以根据不同的需求制定化不同的 RabbitTemplate, 比如每一个 topic 都有自己的 routingKey 规则
 *
 * @auth chenkaixin
 * @date 2021/03/16
 */
@Slf4j
@Component
public class RabbitTemplateContainer implements RabbitTemplate.ConfirmCallback {

    private final Map<String, RabbitTemplate> rabbitMap = Maps.newConcurrentMap();

    private SerializerFactory serializerFactory = JacksonSerializerFactory.INSTANCE;

    private final Splitter splitter = Splitter.on("#");

    private final ConnectionFactory connectionFactory;

    private final MessageStoreService messageStoreService;

    public RabbitTemplateContainer(ConnectionFactory connectionFactory, MessageStoreService messageStoreService) {
        this.connectionFactory = connectionFactory;
        this.messageStoreService = messageStoreService;
    }

    public RabbitTemplate getTemplate(Message message) throws MessageRunTimeException {

        Preconditions.checkNotNull(message);

        String topic = message.getTopic();
        RabbitTemplate rabbitTemplate = rabbitMap.get(topic);
        if (rabbitTemplate != null) {
            return rabbitTemplate;
        }
        log.info("#RabbitTemplateContainer.getTemplate# topic: {} is not exists, create one", topic);

        RabbitTemplate newTemplate = new RabbitTemplate(connectionFactory);
        newTemplate.setExchange(topic);
        newTemplate.setRoutingKey(message.getRoutingKey());
        newTemplate.setRetryTemplate(new RetryTemplate());

        //	添加序列化反序列化和converter对象
        Serializer serializer = serializerFactory.create();
        GenericMessageConverter gmc = new GenericMessageConverter(serializer);
        RabbitMessageConverter rmc = new RabbitMessageConverter(gmc);
        newTemplate.setMessageConverter(rmc);

        String messageType = message.getMessageType();
        if (!MessageType.RAPID.equals(messageType)) {
            newTemplate.setConfirmCallback(this);
        }

        rabbitMap.putIfAbsent(topic, newTemplate);

        return rabbitMap.get(topic);
    }

    /**
     * 无论是 confirm 消息 还是 reliant 消息，发送消息以后 broker 都会去回调 confirm
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {

        // 	具体的消息应答
        List<String> strings = splitter.splitToList(correlationData.getId());
        String messageId = strings.get(0);
        long sendTime = Long.parseLong(strings.get(1));
        String messageType = strings.get(2);
        if (ack) {
            //	当 broker 返回 ack 成功时，就去更新一下日志表里对应的消息发送状态为 SEND_OK
            // 	如果当前消息类型为 reliant 我们就去数据库查找并进行更新
            if (MessageType.RELIANT.endsWith(messageType)) {
                this.messageStoreService.success(messageId);
            }
            log.info("send message is OK, confirm messageId: {}, sendTime: {}", messageId, sendTime);
        } else {
            log.error("send message is Fail, confirm messageId: {}, sendTime: {}", messageId, sendTime);
        }
    }
}