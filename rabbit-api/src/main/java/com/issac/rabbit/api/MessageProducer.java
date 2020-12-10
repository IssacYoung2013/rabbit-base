package com.issac.rabbit.api;

import com.issac.rabbit.api.exception.MessageRuntimeException;

import java.util.List;

/**
 * @author: ywy
 * @date: 2020-11-21
 * @desc:
 */
public interface MessageProducer {

    /**
     * 发送消息附带callback回调
     * @param message
     * @throws MessageRuntimeException
     */
    void send(Message message, SendCallback sendCallback) throws MessageRuntimeException;


    /**
     * 发送消息
     * @param message
     * @throws MessageRuntimeException
     */
    void send(Message message) throws MessageRuntimeException;

    /**
     * 批量发送消息
     * @param messages
     * @throws MessageRuntimeException
     */
    void send(List<Message> messages) throws MessageRuntimeException;
}
