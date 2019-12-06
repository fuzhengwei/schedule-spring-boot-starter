package org.itstack.middleware.schedule.task;

import org.itstack.middleware.schedule.common.Constants;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.config.CronTask;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 博  客：http://bugstack.cn
 * 公众号：bugstack虫洞栈 | 沉淀、分享、成长，让自己和他人都能有所收获！
 * create by 付政委 on 2019/12/4
 */
@Component("itstack-middlware-schedule-cronTaskRegister")
public class CronTaskRegister implements DisposableBean {

    @Resource(name = "itstack-middlware-schedule-taskScheduler")
    private TaskScheduler taskScheduler;

    public TaskScheduler getScheduler() {
        return this.taskScheduler;
    }

    public void addCronTask(SchedulingRunnable task, String cronExpression) {
        if (null != Constants.scheduledTasks.get(task.taskId())) {
            removeCronTask(task.taskId());
        }
        CronTask cronTask = new CronTask(task, cronExpression);
        Constants.scheduledTasks.put(task.taskId(), scheduleCronTask(cronTask));
    }

    public void removeCronTask(String taskId) {
        ScheduledTask scheduledTask = Constants.scheduledTasks.remove(taskId);
        if (scheduledTask == null) return;
        scheduledTask.cancel();
    }

    private ScheduledTask scheduleCronTask(CronTask cronTask) {
        ScheduledTask scheduledTask = new ScheduledTask();
        scheduledTask.future = this.taskScheduler.schedule(cronTask.getRunnable(), cronTask.getTrigger());
        return scheduledTask;
    }

    @Override
    public void destroy() {
        for (ScheduledTask task : Constants.scheduledTasks.values()) {
            task.cancel();
        }
        Constants.scheduledTasks.clear();
    }

}
