package com.issac.rabbit.common.serialize;

/**
 * @author: ywy
 * @date: 2020-11-21
 * @desc:
 */
public interface SerializerFactory {

    /**
     * 创建
     * @return
     */
    Serializer create();
}
