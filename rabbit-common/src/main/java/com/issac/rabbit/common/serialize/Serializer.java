package com.issac.rabbit.common.serialize;

/**
 * @author: ywy
 * @date: 2020-11-21
 * @desc:
 */
public interface Serializer {

    /**
     * 对象 -》 字节数组
     * @param data
     * @return
     */
    byte[] serializeRaw(Object data);

    /**
     * 对象 -》字符串
     * @param data
     * @return
     */
    String  serialize(Object data);

    /**
     * 反序列化
     * @param content
     * @param <T>
     * @return
     */
    <T> T deserialize(String content);

    /**
     * 对象反序列
     * @param content
     * @param <T>
     * @return
     */
    <T> T deserialize(byte[] content);
}
