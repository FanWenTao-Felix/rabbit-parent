package ink.ckx.test;

import ink.ckx.rabbit.api.Message;
import ink.ckx.rabbit.api.MessageType;
import ink.ckx.rabbit.producer.broker.ProducerClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SpringBootTest
class ApplicationTest {

    @Autowired
    private ProducerClient producerClient;

    @Test
    public void testProducerClient() throws Exception {

        for (int i = 0; i < 1; i++) {
            String uniqueId = UUID.randomUUID().toString();
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("name", "张三");
            attributes.put("age", "18");
            Message message = new Message(
                    uniqueId,
                    "exchange-1",
                    "springboot.abc",
                    attributes,
                    0);
            message.setMessageType(MessageType.RELIANT);
            producerClient.send(message);
        }

        Thread.sleep(100000);
    }

    @Test
    public void testProducerClient2() throws Exception {

        for (int i = 0; i < 1; i++) {
            String uniqueId = UUID.randomUUID().toString();
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("name", "陈一");
            attributes.put("age", "20");
            Message message = new Message(
                    uniqueId,
                    "delay-exchange-1",
                    "delay.abc",
                    attributes,
                    20000);
            message.setMessageType(MessageType.RELIANT);
            producerClient.send(message);
        }
        Thread.sleep(100000);
    }
}