package com.issac.rabbit.task.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author: ywy
 * @date: 2020-12-08
 * @desc:
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ElasticJobConfig {

    /**
     * job名称
     *
     * @return
     */
    String name();

    String cron() default "";

    int shardingTotalCount() default 1;

    String shardingItemParameters() default "";

    String jobParameter() default "";

    boolean failover() default false;

    boolean misfire() default true;

    String description() default "";

    boolean overwrite() default false;

    boolean streamingProcess() default false;

    String scriptCommandLine() default "";

    boolean monitorExecution() default false;

    int monitorPort() default -1;

    int maxTimeDiffSeconds() default -1;    //must

    String jobShardingStrategyClass() default "";    //must

    int reconcileIntervalMinutes() default 10;    //must

    String eventTraceRdbDataSource() default "";    //must

    String listener() default "";    //must

    boolean disabled() default false;    //must

    String distributedListener() default "";

    long startedTimeoutMilliseconds() default Long.MAX_VALUE;    //must

    long completedTimeoutMilliseconds() default Long.MAX_VALUE;        //must

    String jobExceptionHandler() default "com.dangdang.ddframe.job.executor.handler.impl.DefaultJobExceptionHandler";

    String executorServiceHandler() default "com.dangdang.ddframe.job.executor.handler.impl.DefaultExecutorServiceHandler";

}
