package com.issac.rabbit.producer.broker;

import com.google.common.base.Preconditions;
import com.issac.rabbit.api.Message;
import com.issac.rabbit.api.MessageProducer;
import com.issac.rabbit.api.MessageType;
import com.issac.rabbit.api.SendCallback;
import com.issac.rabbit.api.exception.MessageRuntimeException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: ywy
 * @date: 2020-11-21
 * @desc: 发送消息的实际实现类
 */
@Component
public class ProducerClient implements MessageProducer {

    @Resource
    RabbitBroker rabbitBroker;

    @Override
    public void send(Message message, SendCallback sendCallback) throws MessageRuntimeException {

    }

    @Override
    public void send(Message message) throws MessageRuntimeException {
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
        }
    }

    @Override
    public void send(List<Message> messages) throws MessageRuntimeException {
        messages.forEach(message -> {
            message.setMessageType(MessageType.RAPID);
            MessageHolder.add(message);
            rabbitBroker.sendMessages();
        });
    }
}
