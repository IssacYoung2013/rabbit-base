package com.issac.rabbit.task.autoconfigure;

import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import com.issac.rabbit.task.parser.ElasticJobConfParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: ywy
 * @date: 2020-12-08
 * @desc:
 */
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "elastic.job.zk", name = {"namespace", "serverLists"}, matchIfMissing = false)
@EnableConfigurationProperties(JobZookeeperProperties.class)
public class JobParserAutoConfiguration {

    @Bean(initMethod = "init")
    public ZookeeperRegistryCenter zookeeperRegistryCenter(JobZookeeperProperties jobZookeeperProperties) {
        ZookeeperConfiguration zookeeperConfiguration = new ZookeeperConfiguration(jobZookeeperProperties.getServerLists()
                , jobZookeeperProperties.getNamespace());
        zookeeperConfiguration.setConnectionTimeoutMilliseconds(jobZookeeperProperties.getConnectionTimeoutMilliseconds());
        zookeeperConfiguration.setSessionTimeoutMilliseconds(jobZookeeperProperties.getSessionTimeoutMilliseconds());
        zookeeperConfiguration.setMaxRetries(jobZookeeperProperties.getMaxRetries());
        zookeeperConfiguration.setBaseSleepTimeMilliseconds(jobZookeeperProperties.getBaseSleepTimeMilliseconds());
        zookeeperConfiguration.setMaxSleepTimeMilliseconds(jobZookeeperProperties.getMaxSleepTimeMilliseconds());
        zookeeperConfiguration.setDigest(jobZookeeperProperties.getDigest());
        log.info("初始化job注册中心配置成功，zkaddress:{},namespace:{}", jobZookeeperProperties.getServerLists(),
                jobZookeeperProperties.getNamespace());
        return new ZookeeperRegistryCenter(zookeeperConfiguration);
    }

    @Bean
    public ElasticJobConfParser elasticJobConfParser(JobZookeeperProperties jobZookeeperProperties,
                                                     ZookeeperRegistryCenter zookeeperRegistryCenter) {
        return new ElasticJobConfParser(jobZookeeperProperties, zookeeperRegistryCenter);
    }
}
