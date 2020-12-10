package com.issac.rabbit.api;

/**
 * @author: ywy
 * @date: 2020-11-21
 * @desc:
 */
public interface MessageListener {

    /**
     * 消费者监听消息
     * @param message
     */
    void onMessage(Message message);
}
