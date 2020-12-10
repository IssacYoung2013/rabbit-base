package com.issac.rabbit.producer.constant;

/**
 * @author: ywy
 * @date: 2020-11-30
 * @desc:
 */
public enum BrokerMessageStatus {
    SENDING("0"),
    SEND_OK("1"),
    SEND_FAIL("2"),
    SEND_FAIL_A_MOMENT("3");

    private String code;

    BrokerMessageStatus(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
