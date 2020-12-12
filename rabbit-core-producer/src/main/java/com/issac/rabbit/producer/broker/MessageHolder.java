package com.issac.rabbit.producer.broker;

import com.google.common.collect.Lists;
import com.issac.rabbit.api.Message;

import java.util.List;

/**
 * @author: ywy
 * @date: 2020-12-12
 * @desc:
 */
public class MessageHolder {

    private List<Message> messages = Lists.newArrayList();

    @SuppressWarnings({"rawtypes","unchecked"})
    public static final ThreadLocal<MessageHolder> holder = new ThreadLocal<MessageHolder>() {
        @Override
        protected MessageHolder initialValue() {
            return new MessageHolder();
        }
    };

    public static void add(Message message) {
        holder.get().messages.add(message);
    }

    public static List<Message> clear() {
        List<Message> tmp = Lists.newArrayList(holder.get().messages);
        holder.remove();
        return tmp;
    }

}
