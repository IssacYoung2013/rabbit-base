package com.issac.rabbit.task.annotation;

import com.issac.rabbit.task.autoconfigure.JobParserAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.lang.annotation.*;

/**
 * @author: ywy
 * @date: 2020-12-08
 * @desc:
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(JobParserAutoConfiguration.class)
public @interface EnableElasticJob {

}
