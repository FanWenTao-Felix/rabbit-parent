package ink.ckx.rabbit.producer.broker;

import com.google.common.collect.Lists;
import ink.ckx.rabbit.api.Message;

import java.util.List;

/**
 * @auth chenkaixin
 * @date 2021/03/16
 */
public class MessageHolder {

    public static final ThreadLocal<MessageHolder> holder = ThreadLocal.withInitial(MessageHolder::new);

    private final List<Message> messages = Lists.newArrayList();

    public static void add(Message message) {
        holder.get().messages.add(message);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static List<Message> clear() {
        List<Message> tmp = Lists.newArrayList(holder.get().messages);
        holder.remove();
        return tmp;
    }
}