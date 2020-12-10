package com.issac.rabbit.api;

import com.issac.rabbit.api.exception.MessageRuntimeException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author: ywy
 * @date: 2020-11-21
 * @desc: 建造者模式
 */
public class MessageBuilder {
    /**
     * 1. 复制属性
     * 2. 构造函数私有化
     * 3. withMsgId 返回return
     */
    private String msgId;
    private String topic;
    private String routingKey = "";
    private Map<String, Object> attributes = new HashMap<>();
    private int delayMills;
    private String messageType = MessageType.CONFIRM;

    public static MessageBuilder create() {
        return new MessageBuilder();
    }

    public MessageBuilder withMessageId(String messageId) {
        this.msgId = messageId;
        return this;
    }

    public MessageBuilder withTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public MessageBuilder withRoutingKey(String routingKey) {
        this.routingKey = routingKey;
        return this;
    }

    public MessageBuilder withAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
        return this;
    }

    public MessageBuilder withAttributes(String key, Object value) {
        this.attributes.put(key, value);
        return this;
    }

    public MessageBuilder withDelayMills(int delayMills) {
        this.delayMills = delayMills;
        return this;
    }

    public MessageBuilder withMessageType(String messageType) {
        this.messageType = messageType;
        return this;
    }

    public Message build() {
        // 1. check msg id
        if (msgId == null) {
            msgId = UUID.randomUUID().toString();
        }
        // 2. topic is null
        if (topic == null) {
            throw new MessageRuntimeException("topic is null");
        }
        return new Message(msgId, topic, routingKey, attributes, delayMills, messageType);
    }
}
