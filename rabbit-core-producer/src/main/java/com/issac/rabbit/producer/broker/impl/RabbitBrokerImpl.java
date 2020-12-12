package com.issac.rabbit.producer.broker.impl;

import com.issac.rabbit.api.Message;
import com.issac.rabbit.api.MessageType;
import com.issac.rabbit.producer.broker.*;
import com.issac.rabbit.producer.constant.BrokerMessageConst;
import com.issac.rabbit.producer.constant.BrokerMessageStatus;
import com.issac.rabbit.producer.entity.BrokerMessage;
import com.issac.rabbit.producer.service.MessageStoreService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author: ywy
 * @date: 2020-11-21
 * @desc:
 */
@Slf4j
@Component
public class RabbitBrokerImpl implements RabbitBroker {

    @Resource
    RabbitTemplateContainer rabbitTemplateContainer;

    @Autowired
    MessageStoreService messageStoreService;

    @Override
    public void rapidSend(Message message) {
        message.setMessageType(MessageType.RAPID);
        sendKernel(message);
    }

    private void sendKernel(Message message) {
        AsyncQueue.submit(new Runnable() {
            @Override
            public void run() {
                CorrelationData correlationData = new CorrelationData(String.format("%s#%s#%s", message.getMessageId(), System.currentTimeMillis(), message.getMessageType()));
                String routingKey = message.getRoutingKey();
                RabbitTemplate rabbitTemplate = rabbitTemplateContainer.getTemplate(message);
                String topic = message.getTopic();
                rabbitTemplate.convertAndSend(topic, routingKey, message, correlationData);
                log.info("#RabbitBrokerImpl.sendKernel#send to rabbitmq, messageId: {}", message.getMessageId());
            }
        });
    }

    @Override
    public void confirmSend(Message message) {
        message.setMessageType(MessageType.CONFIRM);
        sendKernel(message);
    }

    @Override
    public void reliantSend(final Message message) {
        message.setMessageType(MessageType.RELIANT);

        BrokerMessage bm = messageStoreService.selectByMessageId(message.getMessageId());
        Date now = new Date();

        if (bm == null) {
            // 1. 持久化
            BrokerMessage brokerMessage = new BrokerMessage();
            brokerMessage.setMessageId(message.getMessageId());
            brokerMessage.setStatus(BrokerMessageStatus.SENDING.getCode());
            //trycount 最开始发送不需要设置
            brokerMessage.setNextRetry(DateUtils.addMinutes(now, BrokerMessageConst.TIMEOUT));
            brokerMessage.setCreateTime(now);
            brokerMessage.setUpdateTime(now);
            brokerMessage.setMessage(message);
            messageStoreService.insert(brokerMessage);
        } else {

        }

        // 2. 发送
        sendKernel(message);
    }

    @Override
    public void sendMessages() {
        List<Message> messages = MessageHolder.clear();
        messages.forEach(message -> {
            MessageHolderAsyncQueue.submit(new Runnable() {
                @Override
                public void run() {
                    CorrelationData correlationData = new CorrelationData(String.format("%s#%s#%s", message.getMessageId(), System.currentTimeMillis(), message.getMessageType()));
                    String routingKey = message.getRoutingKey();
                    RabbitTemplate rabbitTemplate = rabbitTemplateContainer.getTemplate(message);
                    String topic = message.getTopic();
                    rabbitTemplate.convertAndSend(topic, routingKey, message, correlationData);
                    log.info("#RabbitBrokerImpl.sendKernel#send to rabbitmq, messageId: {}", message.getMessageId());
                }
            });
        });
    }
}
