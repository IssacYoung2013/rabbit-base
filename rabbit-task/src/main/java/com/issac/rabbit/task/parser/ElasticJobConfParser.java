package com.issac.rabbit.task.parser;

import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.JobTypeConfiguration;
import com.dangdang.ddframe.job.config.dataflow.DataflowJobConfiguration;
import com.dangdang.ddframe.job.config.script.ScriptJobConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.event.rdb.JobEventRdbConfiguration;
import com.dangdang.ddframe.job.executor.handler.JobProperties;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import com.issac.rabbit.task.annotation.ElasticJobConfig;
import com.issac.rabbit.task.autoconfigure.JobZookeeperProperties;
import com.issac.rabbit.task.enums.ElasticJobTypeEnums;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StringUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author: ywy
 * @date: 2020-12-08
 * @desc:
 */
@Slf4j
public class ElasticJobConfParser implements ApplicationListener<ApplicationReadyEvent> {

    private JobZookeeperProperties jobZookeeperProperties;

    private ZookeeperRegistryCenter zookeeperRegistryCenter;

    public ElasticJobConfParser(JobZookeeperProperties jobZookeeperProperties, ZookeeperRegistryCenter zookeeperRegistryCenter) {
        this.jobZookeeperProperties = jobZookeeperProperties;
        this.zookeeperRegistryCenter = zookeeperRegistryCenter;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("debug");
        try {

            ApplicationContext applicationContext = event.getApplicationContext();
            Map<String, Object> beanMap = applicationContext.getBeansWithAnnotation(ElasticJobConfig.class);
            for (Iterator it = beanMap.values().iterator(); it.hasNext();) {
                Object confBean = it.next();
                Class<?> clazz = confBean.getClass();
                if (clazz.getName().indexOf("$") > 0) {
                    String className = clazz.getName();
                    clazz = Class.forName(className.substring(0, className.indexOf("$")));
                }
                // 获取接口类型，判断是神了类型的任务
                String jobTypeName = clazz.getInterfaces()[0].getSimpleName();
                // 获取配置项
                ElasticJobConfig conf = clazz.getAnnotation(ElasticJobConfig.class);
                String jobClass = clazz.getName();
                String name = this.jobZookeeperProperties.getNamespace() + "." + conf.name();
                String cron = conf.cron();
                String shardingItemParameters = conf.shardingItemParameters();
                String description = conf.description();
                String jobParameter = conf.jobParameter();
                String jobExceptionHandler = conf.jobExceptionHandler();
                String executorServiceHandler = conf.executorServiceHandler();

                String jobShardingStrategyClass = conf.jobShardingStrategyClass();
                String eventTraceRdbDataSource = conf.eventTraceRdbDataSource();
                String scriptCommandLine = conf.scriptCommandLine();

                boolean failover = conf.failover();
                boolean misfire = conf.misfire();
                boolean overwrite = conf.overwrite();
                boolean disabled = conf.disabled();
                boolean monitorExecution = conf.monitorExecution();
                boolean streamingProcess = conf.streamingProcess();

                int shardingTotalCount = conf.shardingTotalCount();
                int monitorPort = conf.monitorPort();
                int maxTimeDiffSeconds = conf.maxTimeDiffSeconds();
                int reconcileIntervalMinutes = conf.reconcileIntervalMinutes();

                // 先把当当网esjob相关configuration实例化
                JobCoreConfiguration coreConfig = JobCoreConfiguration
                        .newBuilder(name, cron, shardingTotalCount)
                        .shardingItemParameters(shardingItemParameters)
                        .description(description)
                        .failover(failover)
                        .jobParameter(jobParameter)
                        .jobProperties(JobProperties.JobPropertiesEnum.JOB_EXCEPTION_HANDLER.getKey(), jobExceptionHandler)
                        .jobProperties(JobProperties.JobPropertiesEnum.EXECUTOR_SERVICE_HANDLER.getKey(), executorServiceHandler)
                        .misfire(misfire)
                        .build();

                JobTypeConfiguration typeConfig = null;
                if (ElasticJobTypeEnums.SIMPLE.getType().equals(jobTypeName)) {
                    typeConfig = new SimpleJobConfiguration(coreConfig, jobClass);
                }
                if (ElasticJobTypeEnums.DATAFLOW.getType().equals(jobTypeName)) {
                    typeConfig = new DataflowJobConfiguration(coreConfig, jobClass, conf.streamingProcess());

                }
                if (ElasticJobTypeEnums.SCRIPT.getType().equals(jobTypeName)) {
                    typeConfig = new ScriptJobConfiguration(coreConfig, scriptCommandLine);
                }

                // LiteJobConfiguration
                LiteJobConfiguration jobConfig = LiteJobConfiguration.newBuilder(typeConfig)
                        .overwrite(conf.overwrite())
                        .disabled(conf.disabled())
                        .monitorPort(conf.monitorPort())
                        .monitorExecution(conf.monitorExecution())
                        .maxTimeDiffSeconds(conf.maxTimeDiffSeconds())
                        .reconcileIntervalMinutes(conf.reconcileIntervalMinutes())
                        .build();

                // 创建一个Spring 的 BeanDefinition
                BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(SpringJobScheduler.class);
                factory.setInitMethodName("init");
                factory.setScope("prototype");
                //	1.添加bean构造参数，相当于添加自己的真实的任务实现类
                if (!ElasticJobTypeEnums.SCRIPT.getType().equals(jobTypeName)) {
                    factory.addConstructorArgValue(confBean);
                }
                //	2.添加注册中心
                factory.addConstructorArgValue(this.zookeeperRegistryCenter);
                //	3.添加LiteJobConfiguration
                factory.addConstructorArgValue(jobConfig);

                //	4.如果有eventTraceRdbDataSource 则也进行添加
                if (StringUtils.hasText(eventTraceRdbDataSource)) {
                    BeanDefinitionBuilder rdbFactory = BeanDefinitionBuilder.rootBeanDefinition(JobEventRdbConfiguration.class);
                    rdbFactory.addConstructorArgReference(eventTraceRdbDataSource);
                    factory.addConstructorArgValue(rdbFactory.getBeanDefinition());
                }

                //  5.添加监听
                List<?> elasticJobListeners = getTargetElasticJobListeners(conf);
                factory.addConstructorArgValue(elasticJobListeners);

                // 	接下来就是把factory 也就是 SpringJobScheduler注入到Spring容器中
                DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();

                String registerBeanName = conf.name() + "SpringJobScheduler";
                defaultListableBeanFactory.registerBeanDefinition(registerBeanName, factory.getBeanDefinition());
                SpringJobScheduler scheduler = (SpringJobScheduler) applicationContext.getBean(registerBeanName);
                scheduler.init();
                log.info("启动elastic-job作业: " + name);
            }
            log.info("共计启动elastic-job作业数量为: {} 个", beanMap.values().size());
        } catch (Exception e) {
            log.error("elasticjob 启动异常, 系统强制退出", e);
            System.exit(1);
        }
    }

    private List<BeanDefinition> getTargetElasticJobListeners(ElasticJobConfig conf) {
        List<BeanDefinition> result = new ManagedList<BeanDefinition>(2);
        String listeners = conf.listener();
        if (StringUtils.hasText(listeners)) {
            BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(listeners);
            factory.setScope("prototype");
            result.add(factory.getBeanDefinition());
        }

        String distributedListeners = conf.distributedListener();
        long startedTimeoutMilliseconds = conf.startedTimeoutMilliseconds();
        long completedTimeoutMilliseconds = conf.completedTimeoutMilliseconds();

        if (StringUtils.hasText(distributedListeners)) {
            BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(distributedListeners);
            factory.setScope("prototype");
            factory.addConstructorArgValue(Long.valueOf(startedTimeoutMilliseconds));
            factory.addConstructorArgValue(Long.valueOf(completedTimeoutMilliseconds));
            result.add(factory.getBeanDefinition());
        }
        return result;
    }

}