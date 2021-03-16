package ink.ckx.rabbit.producer.broker;

import com.google.common.base.Preconditions;
import ink.ckx.rabbit.api.Message;
import ink.ckx.rabbit.api.MessageProducer;
import ink.ckx.rabbit.api.MessageType;
import ink.ckx.rabbit.api.SendCallback;
import ink.ckx.rabbit.api.exception.MessageRunTimeException;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 发送消息的实际实现类
 *
 * @auth chenkaixin
 * @date 2021/03/16
 */
@Component
public class ProducerClient implements MessageProducer {

    private final RabbitBroker rabbitBroker;

    public ProducerClient(RabbitBroker rabbitBroker) {
        this.rabbitBroker = rabbitBroker;
    }

    @Override
    public void send(Message message) throws MessageRunTimeException {

        Preconditions.checkNotNull(message.getTopic());

        String messageType = message.getMessageType();
        switch (messageType) {
            case MessageType.RAPID:
                rabbitBroker.rapidSend(message);
                break;
            case MessageType.CONFIRM:
                rabbitBroker.confirmSend(message);
                break;
            case MessageType.RELIANT:
                rabbitBroker.reliantSend(message);
                break;
            default:
                break;
        }
    }

    @Override
    public void send(List<Message> messages) throws MessageRunTimeException {

        messages.forEach(message -> {
            message.setMessageType(MessageType.RAPID);
            MessageHolder.add(message);
        });
        rabbitBroker.sendMessages();
    }

    @Override
    public void send(Message message, SendCallback sendCallback) throws MessageRunTimeException {
    }
}