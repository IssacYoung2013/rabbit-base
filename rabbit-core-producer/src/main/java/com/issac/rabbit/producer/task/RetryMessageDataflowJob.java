package com.issac.rabbit.producer.task;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.dataflow.DataflowJob;
import com.issac.rabbit.producer.broker.RabbitBroker;
import com.issac.rabbit.producer.constant.BrokerMessageStatus;
import com.issac.rabbit.producer.entity.BrokerMessage;
import com.issac.rabbit.producer.service.MessageStoreService;
import com.issac.rabbit.task.annotation.ElasticJobConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: ywy
 * @date: 2020-12-08
 * @desc:
 */
@Component
@ElasticJobConfig(
        name = "com.issac.rabbit.producer.task.RetryMessageDataflowJob",
        cron = "0/10 * * * * ?",
        description = "可靠性投递消息补偿任务",
        overwrite = true,
        shardingTotalCount = 1
)
@Slf4j
public class RetryMessageDataflowJob implements DataflowJob<BrokerMessage> {

    public static final int MAX_RETRY_COUNT = 3;

    @Resource
    private MessageStoreService messageStoreService;

    @Autowired
    private RabbitBroker rabbitBroker;

    @Override
    public List<BrokerMessage> fetchData(ShardingContext shardingContext) {
        List<BrokerMessage> list = messageStoreService.fetchTimeoutMessage4Retry(BrokerMessageStatus.SENDING);
        log.info("抓取数据集合，数量：" + list.size());
        return list;
    }

    @Override
    public void processData(ShardingContext shardingContext, List<BrokerMessage> dataList) {
        dataList.forEach(brokerMessage -> {
            String messageId = brokerMessage.getMessageId();
            if (brokerMessage.getRetryCount() >= MAX_RETRY_COUNT) {
                this.messageStoreService.failure(messageId);
                log.warn("----- 消息重试最终失败，消息设置为最终失败，消息id:{}", messageId);
            } else {
                // 每次重发的时候更新trycount
                this.messageStoreService.updateTryCount(messageId);
                this.rabbitBroker.reliantSend(brokerMessage.getMessage());
            }
        });
    }
}
