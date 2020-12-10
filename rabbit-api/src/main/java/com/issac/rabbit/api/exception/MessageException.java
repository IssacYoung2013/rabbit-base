package com.issac.rabbit.api.exception;

/**
 * @author: ywy
 * @date: 2020-11-21
 * @desc:
 */
public class MessageException extends Exception {
    private static final long serialVersionUID = 6050309778994299375L;

    public MessageException() {
        super();
    }

    public MessageException(String message) {
        super(message);
    }

    public MessageException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessageException(Throwable cause) {
        super(cause);
    }
}
