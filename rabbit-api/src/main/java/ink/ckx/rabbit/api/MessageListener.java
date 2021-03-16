package ink.ckx.rabbit.api;

/**
 * @auth chenkaixin
 * @date 2021/03/16
 */
public interface MessageListener {

    void onMessage(Message message);
}