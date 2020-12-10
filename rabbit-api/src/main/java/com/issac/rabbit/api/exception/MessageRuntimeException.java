package com.issac.rabbit.api.exception;

/**
 * @author: ywy
 * @date: 2020-11-21
 * @desc:
 */
public class MessageRuntimeException extends RuntimeException {

    private static final long serialVersionUID = -8918405531957502981L;

    public MessageRuntimeException() {
    }

    public MessageRuntimeException(String message) {
        super(message);
    }

    public MessageRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessageRuntimeException(Throwable cause) {
        super(cause);
    }
}
