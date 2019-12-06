package org.itstack.middleware.schedule.task;

import org.itstack.middleware.schedule.common.Constants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * http://www.itstack.org
 * create by fuzhengwei on 2019/12/4
 */
@Configuration("itstack-middlware-schedule-schedulingConfig")
public class SchedulingConfig {

    @Bean("itstack-middlware-schedule-taskScheduler")
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(Constants.Global.schedulePoolSize);
        taskScheduler.setRemoveOnCancelPolicy(true);
        taskScheduler.setThreadNamePrefix("ItStackMiddlewareScheduleThreadPool-");
        return taskScheduler;
    }

}
