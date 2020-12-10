package com.issac.rabbit.producer.broker;

import com.issac.rabbit.api.Message;

/**
 * @author: ywy
 * @date: 2020-11-21
 * @desc: 具体发送消息的接口
 */
public interface RabbitBroker {
    /**
     * 迅速消息
     * @param message
     */
    void rapidSend(Message message);

    /**
     * 发送确认消息
     * @param message
     */
    void confirmSend(Message message);

    /**
     * 发送可靠消息
     * @param message
     */
    void reliantSend(Message message);
}
