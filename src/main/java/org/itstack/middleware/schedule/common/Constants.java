package org.itstack.middleware.schedule.common;

import org.apache.curator.framework.CuratorFramework;
import org.itstack.middleware.schedule.domain.ExecOrder;
import org.itstack.middleware.schedule.task.ScheduledTask;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 博  客：http://bugstack.cn
 * 公众号：bugstack虫洞栈 | 沉淀、分享、成长，让自己和他人都能有所收获！
 * create by 付政委 on @2019
 */
public class Constants {

    //任务组；beanName->ExecOrder
    public static final Map<String, List<ExecOrder>> execOrderMap = new ConcurrentHashMap<>();
    public static final Map<String, ScheduledTask> scheduledTasks = new ConcurrentHashMap<>(16);

    public static class Global {
        public static ApplicationContext applicationContext;
        public static final String LINE = "/";
        public static String CHARSET_NAME = "utf-8";
        public static int schedulePoolSize = 8;     //定时任务执行线程池核心线程数
        public static String ip;                    //本机IP
        public static String zkAddress;             //zookeeper服务地址；x.x.x.x:2181
        public static String schedulerServerId;     //任务服务ID；  工程名称En
        public static String schedulerServerName;   //任务服务名称；工程名称Ch
        public static CuratorFramework client;      //zk配置；client
        public static String path_root = "/org/itstack/middleware/schedule";   //zk配置；根目录
        public static String path_root_exec = path_root + "/exec";
        public static String path_root_server;
        public static String path_root_server_ip;
        public static String path_root_server_ip_clazz;              //[结构标记]类名称
        public static String path_root_server_ip_clazz_method;       //[结构标记]临时节点
        public static String path_root_server_ip_clazz_method_status;//[结构标记]永久节点
    }

    public static class InstructStatus{
        public final static Integer stop = 0;     //停止
        public final static Integer Start = 1;    //启动
        public final static Integer Refresh = 2;  //刷新
    }

}
