package org.itstack.middleware.schedule.config;

import com.alibaba.fastjson.JSON;
import org.apache.curator.framework.CuratorFramework;
import org.itstack.middleware.schedule.annotation.DcsScheduled;
import org.itstack.middleware.schedule.common.Constants;
import org.itstack.middleware.schedule.domain.ExecOrder;
import org.itstack.middleware.schedule.service.HeartbeatService;
import org.itstack.middleware.schedule.service.ZkCuratorServer;
import org.itstack.middleware.schedule.task.CronTaskRegister;
import org.itstack.middleware.schedule.task.SchedulingRunnable;
import org.itstack.middleware.schedule.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.itstack.middleware.schedule.common.Constants.Global.*;

/**
 * 博  客：http://bugstack.cn
 * 公众号：bugstack虫洞栈 | 沉淀、分享、成长，让自己和他人都能有所收获！
 * create by 付政委 on @2019
 */
public class DcsSchedulingConfiguration implements ApplicationContextAware, BeanPostProcessor, ApplicationListener<ContextRefreshedEvent> {

    private Logger logger = LoggerFactory.getLogger(DcsSchedulingConfiguration.class);

    private final Set<Class<?>> nonAnnotatedClasses = Collections.newSetFromMap(new ConcurrentHashMap<>(64));

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Constants.Global.applicationContext = applicationContext;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(bean);
        if (this.nonAnnotatedClasses.contains(targetClass)) return bean;
        Method[] methods = ReflectionUtils.getAllDeclaredMethods(bean.getClass());
        if (methods == null) return bean;
        for (Method method : methods) {
            DcsScheduled dcsScheduled = AnnotationUtils.findAnnotation(method, DcsScheduled.class);
            if (null == dcsScheduled || 0 == method.getDeclaredAnnotations().length) continue;
            List<ExecOrder> execOrderList = Constants.execOrderMap.computeIfAbsent(beanName, k -> new ArrayList<>());
            ExecOrder execOrder = new ExecOrder();
            execOrder.setBean(bean);
            execOrder.setBeanName(beanName);
            execOrder.setMethodName(method.getName());
            execOrder.setDesc(dcsScheduled.desc());
            execOrder.setCron(dcsScheduled.cron());
            execOrder.setAutoStartup(dcsScheduled.autoStartup());
            execOrderList.add(execOrder);
            this.nonAnnotatedClasses.add(targetClass);
        }
        return bean;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        try {
            ApplicationContext applicationContext = contextRefreshedEvent.getApplicationContext();
            //1. 初始化配置
            init_config(applicationContext);
            //2. 初始化服务
            init_server(applicationContext);
            //3. 启动任务
            init_task(applicationContext);
            //4. 挂载节点
            init_node();
            //5. 心跳监听
            HeartbeatService.getInstance().startFlushScheduleStatus();
            logger.info("itstack middleware schedule init config、server、task、node、heart done!");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //1. 初始化配置
    private void init_config(ApplicationContext applicationContext) {
        try {
            StarterServiceProperties properties = applicationContext.getBean("itstack-middlware-schedule-starterAutoConfig", StarterAutoConfig.class).getProperties();
            Constants.Global.zkAddress = properties.getZkAddress();
            Constants.Global.schedulerServerId = properties.getSchedulerServerId();
            Constants.Global.schedulerServerName = properties.getSchedulerServerName();
            InetAddress id = InetAddress.getLocalHost();
            Constants.Global.ip = id.getHostAddress();
        } catch (Exception e) {
            logger.error("itstack middleware schedule init config error！", e);
            throw new RuntimeException(e);
        }
    }

    //2. 初始化服务
    private void init_server(ApplicationContext applicationContext) {
        try {
            //获取zk连接
            CuratorFramework client = ZkCuratorServer.getClient(Constants.Global.zkAddress);
            //节点组装
            path_root_server = StrUtil.joinStr(path_root, LINE, "server", LINE, schedulerServerId);
            path_root_server_ip = StrUtil.joinStr(path_root_server, LINE, "ip", LINE, Constants.Global.ip);
            //创建节点&递归删除本服务IP下的旧内容
            ZkCuratorServer.deletingChildrenIfNeeded(client, path_root_server_ip);
            ZkCuratorServer.createNode(client, path_root_server_ip);
            ZkCuratorServer.setData(client, path_root_server, schedulerServerName);
            //添加节点&监听
            ZkCuratorServer.createNodeSimple(client, Constants.Global.path_root_exec);
            ZkCuratorServer.addTreeCacheListener(applicationContext, client, Constants.Global.path_root_exec);
        } catch (Exception e) {
            logger.error("itstack middleware schedule init server error！", e);
            throw new RuntimeException(e);
        }
    }

    //3. 启动任务
    private void init_task(ApplicationContext applicationContext) {
        CronTaskRegister cronTaskRegistrar = applicationContext.getBean("itstack-middlware-schedule-cronTaskRegister", CronTaskRegister.class);
        Set<String> beanNames = Constants.execOrderMap.keySet();
        for (String beanName : beanNames) {
            List<ExecOrder> execOrderList = Constants.execOrderMap.get(beanName);
            for (ExecOrder execOrder : execOrderList) {
                if (!execOrder.getAutoStartup()) continue;
                SchedulingRunnable task = new SchedulingRunnable(execOrder.getBean(), execOrder.getBeanName(), execOrder.getMethodName());
                cronTaskRegistrar.addCronTask(task, execOrder.getCron());
            }
        }
    }

    //4. 挂载节点
    private void init_node() throws Exception {
        Set<String> beanNames = Constants.execOrderMap.keySet();
        for (String beanName : beanNames) {
            List<ExecOrder> execOrderList = Constants.execOrderMap.get(beanName);
            for (ExecOrder execOrder : execOrderList) {
                String path_root_server_ip_clazz = StrUtil.joinStr(path_root_server_ip, LINE, "clazz", LINE, execOrder.getBeanName());
                String path_root_server_ip_clazz_method = StrUtil.joinStr(path_root_server_ip_clazz, LINE, "method", LINE, execOrder.getMethodName());
                String path_root_server_ip_clazz_method_status = StrUtil.joinStr(path_root_server_ip_clazz, LINE, "method", LINE, execOrder.getMethodName(), "/status");
                //添加节点
                ZkCuratorServer.createNodeSimple(client, path_root_server_ip_clazz);
                ZkCuratorServer.createNodeSimple(client, path_root_server_ip_clazz_method);
                ZkCuratorServer.createNodeSimple(client, path_root_server_ip_clazz_method_status);
                //添加节点数据[临时]
                ZkCuratorServer.appendPersistentData(client, path_root_server_ip_clazz_method + "/value", JSON.toJSONString(execOrder));
                //添加节点数据[永久]
                ZkCuratorServer.setData(client, path_root_server_ip_clazz_method_status, execOrder.getAutoStartup() ? "1" : "0");
            }
        }
    }

}
