package com.issac.rabbit.producer.config.database;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

/**
 * @author: ywy
 * @date: 2020-11-25
 * @desc: 数据库表结构创建
 */
@Configuration
@Slf4j
public class BrokerMessageConfiguration {

    @Autowired
    private DataSource rabbitProducerDataSource;

    @Value("classpath:rabbit-producer-message.sql")
    private Resource schemeScript;

    @Bean
    public DataSourceInitializer initDataSourceInitializer() {
        log.error("----------rabbitProducerDataSource---------:" + rabbitProducerDataSource);
        final DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(rabbitProducerDataSource);
        initializer.setDatabasePopulator(databasePopulator());
        return initializer;
    }

    private DatabasePopulator databasePopulator() {
        final ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(schemeScript);
        return populator;
    }
}
