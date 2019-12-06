package org.itstack.middleware.schedule.task;

import java.util.concurrent.ScheduledFuture;

/**
 * 博  客：http://bugstack.cn
 * 公众号：bugstack虫洞栈 | 沉淀、分享、成长，让自己和他人都能有所收获！
 * create by 付政委 on @2019
 */
public class ScheduledTask {

    volatile ScheduledFuture<?> future;

    /**
     * 取消定时任务
     */
    public void cancel() {
        ScheduledFuture<?> future = this.future;
        if (future == null) return;
        future.cancel(true);
    }

    public boolean isCancelled() {
        ScheduledFuture<?> future = this.future;
        if (future == null) return true;
        return future.isCancelled();
    }

}
