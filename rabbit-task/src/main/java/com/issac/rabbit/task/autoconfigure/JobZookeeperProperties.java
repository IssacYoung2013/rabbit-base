package com.issac.rabbit.task.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author: ywy
 * @date: 2020-12-08
 * @desc:
 */
@Data
@ConfigurationProperties(prefix = "elastic.job.zk")
public class JobZookeeperProperties {

    private String namespace;

    private String serverLists;

    private int maxRetries = 3;

    private int connectionTimeoutMilliseconds = 15000;

    private int sessionTimeoutMilliseconds = 60000;

    private int baseSleepTimeMilliseconds = 1000;

    private int maxSleepTimeMilliseconds = 3000;

    private String digest = "";
}
