package org.itstack.middleware.schedule.annotation;

import org.itstack.middleware.schedule.DoJoinPoint;
import org.itstack.middleware.schedule.config.DcsSchedulingConfiguration;
import org.itstack.middleware.schedule.task.CronTaskRegister;
import org.itstack.middleware.schedule.task.SchedulingConfig;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 博  客：http://bugstack.cn
 * 公众号：bugstack虫洞栈 | 沉淀、分享、成长，让自己和他人都能有所收获！
 * create by 付政委 on @2019
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({DcsSchedulingConfiguration.class})
@ImportAutoConfiguration({SchedulingConfig.class, CronTaskRegister.class, DoJoinPoint.class})
@ComponentScan("org.itstack.middleware.*")
public @interface EnableDcsScheduling {
}
