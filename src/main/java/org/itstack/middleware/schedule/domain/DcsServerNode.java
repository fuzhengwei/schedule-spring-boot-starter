package org.itstack.middleware.schedule.domain;

/**
 * 博  客：http://bugstack.cn
 * 公众号：bugstack虫洞栈 | 沉淀、分享、成长，让自己和他人都能有所收获！
 * create by 付政委 on @2019
 */
public class DcsServerNode {

    private String schedulerServerId;       //任务服务ID；  工程名称En
    private String schedulerServerName;     //任务服务名称；工程名称Ch

    public DcsServerNode(String schedulerServerId, String schedulerServerName) {
        this.schedulerServerId = schedulerServerId;
        this.schedulerServerName = schedulerServerName;
    }

    public String getSchedulerServerId() {
        return schedulerServerId;
    }

    public void setSchedulerServerId(String schedulerServerId) {
        this.schedulerServerId = schedulerServerId;
    }

    public String getSchedulerServerName() {
        return schedulerServerName;
    }

    public void setSchedulerServerName(String schedulerServerName) {
        this.schedulerServerName = schedulerServerName;
    }
}
