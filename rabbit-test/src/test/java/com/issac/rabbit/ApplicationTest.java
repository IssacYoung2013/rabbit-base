package com.issac.rabbit;

import com.issac.rabbit.api.Message;
import com.issac.rabbit.api.MessageType;
import com.issac.rabbit.producer.broker.ProducerClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author: ywy
 * @date: 2020-12-11
 * @desc:
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTest {

    @Autowired
    private ProducerClient producerClient;

    @Test
    public void testProducerClient() throws InterruptedException {
        for (int i = 0; i < 1; i++) {
            String uniqueId = UUID.randomUUID().toString();
            Map<String,Object> attributes = new HashMap<String,Object>();
            attributes.put("name","张三");
            attributes.put("age",18);
            Message message = new Message(uniqueId,"exchange-1","springboot.abc",attributes,0);
            message.setMessageType(MessageType.RELIANT);
            message.setDelayMills(5000);
            producerClient.send(message);
        }
        Thread.sleep(100000);
    }
}