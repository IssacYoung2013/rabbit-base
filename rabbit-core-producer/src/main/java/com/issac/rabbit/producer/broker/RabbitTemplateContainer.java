package com.issac.rabbit.producer.broker;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.issac.rabbit.api.Message;
import com.issac.rabbit.api.MessageType;
import com.issac.rabbit.api.exception.MessageRuntimeException;
import com.issac.rabbit.common.converter.GenericMessageConverter;
import com.issac.rabbit.common.converter.RabbitMessageConverter;
import com.issac.rabbit.common.serialize.JacksonSerializerFactory;
import com.issac.rabbit.common.serialize.Serializer;
import com.issac.rabbit.common.serialize.SerializerFactory;
import com.issac.rabbit.producer.service.MessageStoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author: ywy
 * @date: 2020-11-21
 * @desc: 池化封装
 * 1. 提高发送效率
 * 2. 可以根据不同需求制定不同的template
 */
@Slf4j
@Component
public class RabbitTemplateContainer implements RabbitTemplate.ConfirmCallback {

    @Resource
    private ConnectionFactory connectionFactory;

    private SerializerFactory serializerFactory = JacksonSerializerFactory.INSTANCE;

    private Splitter splitter = Splitter.on("#");

    @Autowired
    MessageStoreService messageStoreService;

    /**
     * 池化操作
     */
    private Map<String, RabbitTemplate> rabbitMap = Maps.newConcurrentMap();

    public RabbitTemplate getTemplate(Message message) throws MessageRuntimeException {
        Preconditions.checkNotNull(message);
        String topic = message.getTopic();
        RabbitTemplate rabbitTemplate = rabbitMap.get(topic);
        if (rabbitTemplate != null) {
            return rabbitTemplate;
        }
        log.info("#RabbitTemplateContainer.getTemplate# topic: {} not exist", topic);
        RabbitTemplate newTemplate = new RabbitTemplate(connectionFactory);
        newTemplate.setExchange(topic);
        newTemplate.setRoutingKey(message.getRoutingKey());
        newTemplate.setRetryTemplate(new RetryTemplate());

        // 对于message 序列化类型
        Serializer serializer = serializerFactory.create();
        GenericMessageConverter gmc = new GenericMessageConverter(serializer);
        RabbitMessageConverter rmc = new RabbitMessageConverter(gmc);
        newTemplate.setMessageConverter(rmc);

        String messageType = message.getMessageType();
        if (!MessageType.RAPID.equals(messageType)) {
            newTemplate.setConfirmCallback(this);
        }
        rabbitMap.putIfAbsent(topic, newTemplate);
        return newTemplate;
    }

    /**
     * 无论confirm 还是reliant 发送消息后，都会回调broker
     *
     * @param correlationData
     * @param ack
     * @param s
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String s) {
        // 具体消息应答
        List<String> strings = splitter.splitToList(correlationData.getId());

        String messageId = strings.get(0);
        long sendTime = Long.parseLong(strings.get(1));
        String msgType = strings.get(2);

        if (ack) {
            // 更新日志表消息状态为

            // 如果当前消息是reliant，数据库查找进行更新
            if(MessageType.RELIANT.equals(msgType)) {
                this.messageStoreService.success(messageId);
            }
            log.info("send message is OK, confirm messageId:{},sendTime:{}", messageId, sendTime);
        } else {
            log.info("send message is failure, confirm messageId:{},sendTime:{}", messageId, sendTime);
        }
    }
}
