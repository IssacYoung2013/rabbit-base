package com.issac.rabbit.api;

/**
 * @author: ywy
 * @date: 2020-11-21
 * @desc:
 */
public class MessageType {
    /**
     * 迅速消息：不需要保障消息的可靠性，也不需要做confirm确认
     */
    public final static String RAPID = "0";

    /**
     * 确认消息：不需要保障消息的可靠性，但是会做消息的confirm确认
     */
    public static final String CONFIRM = "1";

    /**
     * 可靠性消息：一定要保障消息的 100% 可靠性投递，不允许任何消息的丢失
     * PS：保障数据库和所发的消息是原子性（最终一致）
     */
    public static final String RELIANT = "2";
}
