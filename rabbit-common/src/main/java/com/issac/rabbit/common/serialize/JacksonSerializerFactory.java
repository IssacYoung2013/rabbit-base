package com.issac.rabbit.common.serialize;

import com.issac.rabbit.api.Message;

/**
 * @author: ywy
 * @date: 2020-11-21
 * @desc:
 */
public class JacksonSerializerFactory implements SerializerFactory {

    public static final JacksonSerializerFactory INSTANCE = new JacksonSerializerFactory();

    @Override
    public Serializer create() {
        return JacksonSerializer.createParametricType(Message.class);
    }
}
