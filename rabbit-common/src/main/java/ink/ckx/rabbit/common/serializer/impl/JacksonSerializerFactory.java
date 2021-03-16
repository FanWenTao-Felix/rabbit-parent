package ink.ckx.rabbit.common.serializer.impl;

import ink.ckx.rabbit.api.Message;
import ink.ckx.rabbit.common.serializer.Serializer;
import ink.ckx.rabbit.common.serializer.SerializerFactory;

public class JacksonSerializerFactory implements SerializerFactory {

    public static final SerializerFactory INSTANCE = new JacksonSerializerFactory();

    @Override
    public Serializer create() {
        return JacksonSerializer.createParametricType(Message.class);
    }
}