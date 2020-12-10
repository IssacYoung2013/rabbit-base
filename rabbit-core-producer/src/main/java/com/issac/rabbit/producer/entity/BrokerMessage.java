package com.issac.rabbit.producer.entity;

import com.issac.rabbit.api.Message;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author: ywy
 * @date: 2020-11-23
 * @desc:
 */
@Data
public class BrokerMessage implements Serializable {
    private static final long serialVersionUID = 3423874172451731849L;
    private String messageId;
    private Message message;
    private Integer retryCount = 0;
    private String status;
    private Date nextRetry;
    private Date createTime;
    private Date updateTime;
}
