package org.itstack.middleware.schedule.domain;

/**
 * 博  客：http://bugstack.cn
 * 公众号：bugstack虫洞栈 | 沉淀、分享、成长，让自己和他人都能有所收获！
 * create by 付政委 on @2019
 */
public class DcsScheduleInfo {

    private String ip;                      //机器IP
    private String schedulerServerId;       //任务服务ID；  工程名称En
    private String schedulerServerName;     //任务服务名称；工程名称Ch
    private String beanName;                //类对象名称
    private String methodName;              //方法名称
    private String desc;                    //任务描述
    private String cron;                    //任务执行
    private Integer status;                 //任务状态；0关闭、1开启、2宕机

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
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

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
