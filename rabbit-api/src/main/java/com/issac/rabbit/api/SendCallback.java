package com.issac.rabbit.api;

/**
 * @author: ywy
 * @date: 2020-11-21
 * @desc:
 */
public interface SendCallback {
    /**
     * 成功回调
     */
    void onSuccess();

    /**
     * 失败回调
     */
    void onFailure();
}
