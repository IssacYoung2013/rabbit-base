package com.issac.rabbit.producer.service;

import com.issac.rabbit.producer.constant.BrokerMessageStatus;
import com.issac.rabbit.producer.entity.BrokerMessage;
import com.issac.rabbit.producer.mapper.BrokerMessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author: ywy
 * @date: 2020-11-30
 * @desc:
 */
@Service
public class MessageStoreService {

    @Autowired
    private BrokerMessageMapper brokerMessageMapper;

    public BrokerMessage selectByMessageId(String messageId) {
        return this.brokerMessageMapper.selectByPrimaryKey(messageId);
    }

    public int insert(BrokerMessage brokerMessage) {
        return this.brokerMessageMapper.insert(brokerMessage);
    }

    public void success(String messageId) {
        this.brokerMessageMapper.changeBrokerMessageStatus(messageId, BrokerMessageStatus.SEND_OK.getCode(),new Date());
    }

    public void failure(String messageId) {
        this.brokerMessageMapper.changeBrokerMessageStatus(messageId, BrokerMessageStatus.SEND_FAIL.getCode(),new Date());
    }

    public List<BrokerMessage> fetchTimeoutMessage4Retry(BrokerMessageStatus brokerMessageStatus) {
        return this.brokerMessageMapper.queryBrokerMessageStatus4Timeout(brokerMessageStatus.getCode());
    }

    public void updateTryCount(String messageId) {
        this.brokerMessageMapper.update4TryCount(messageId,new Date());
    }
}
