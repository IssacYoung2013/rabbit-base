package com.issac.rabbit.common.converter;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;

/**
 * @author: ywy
 * @date: 2020-11-21
 * @desc: 装饰者模式
 */
public class RabbitMessageConverter implements MessageConverter {

    private GenericMessageConverter delegate;

    private final String defaultExpire = String.valueOf(24 * 60 * 60 * 1000);

    public RabbitMessageConverter(GenericMessageConverter delegate) {
        this.delegate = delegate;
    }

    @Override
    public Message toMessage(Object o, MessageProperties messageProperties) throws MessageConversionException {
        messageProperties.setExpiration(defaultExpire);
        return this.delegate.toMessage(o, messageProperties);
    }

    @Override
    public Object fromMessage(Message message) throws MessageConversionException {
        com.issac.rabbit.api.Message msg = (com.issac.rabbit.api.Message) this.delegate.fromMessage(message);
        return msg;
    }
}
