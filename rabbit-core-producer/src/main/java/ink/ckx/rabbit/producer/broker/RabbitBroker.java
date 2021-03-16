package ink.ckx.rabbit.producer.broker;

import ink.ckx.rabbit.api.Message;

/**
 * @auth chenkaixin
 * @date 2021/03/16
 */
public interface RabbitBroker {

    void rapidSend(Message message);

    void confirmSend(Message message);

    void reliantSend(Message message);

    void sendMessages();
}