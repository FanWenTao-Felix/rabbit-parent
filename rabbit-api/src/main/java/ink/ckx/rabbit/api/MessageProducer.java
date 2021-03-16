package ink.ckx.rabbit.api;

import ink.ckx.rabbit.api.exception.MessageRunTimeException;

import java.util.List;

/**
 * @auth chenkaixin
 * @date 2021/03/16
 */
public interface MessageProducer {

    /**
     * 消息的发送，附带 SendCallback 回调执行响应的业务逻辑处理
     *
     * @param message
     * @param sendCallback
     * @throws MessageRunTimeException
     */
    void send(Message message, SendCallback sendCallback) throws MessageRunTimeException;

    /**
     * @param message
     * @throws MessageRunTimeException
     */
    void send(Message message) throws MessageRunTimeException;

    /**
     * 消息的批量发送
     *
     * @param messages
     * @throws MessageRunTimeException
     */
    void send(List<Message> messages) throws MessageRunTimeException;
}