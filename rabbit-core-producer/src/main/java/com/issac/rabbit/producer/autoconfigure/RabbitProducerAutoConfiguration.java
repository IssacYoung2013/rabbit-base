package com.issac.rabbit.producer.autoconfigure;

import com.issac.rabbit.task.annotation.EnableElasticJob;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author: ywy
 * @date: 2020-11-21
 * @desc: 自动装配
 */
@EnableElasticJob
@Configuration
@ComponentScan({"com.issac.rabbit.producer.*"})
public class RabbitProducerAutoConfiguration {

}
