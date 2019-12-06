package org.itstack.middleware.schedule.service;

import com.alibaba.fastjson.JSON;
import org.itstack.middleware.schedule.common.Constants;
import org.itstack.middleware.schedule.domain.ExecOrder;
import org.itstack.middleware.schedule.task.ScheduledTask;
import org.itstack.middleware.schedule.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.itstack.middleware.schedule.common.Constants.Global.*;
import static org.itstack.middleware.schedule.common.Constants.Global.LINE;

/**
 * 博  客：http://bugstack.cn
 * 公众号：bugstack虫洞栈 | 沉淀、分享、成长，让自己和他人都能有所收获！
 * create by 付政委 on @2019
 */
public class HeartbeatService {

    private Logger logger = LoggerFactory.getLogger(HeartbeatService.class);

    private ScheduledExecutorService ses;

    private static class SingletonHolder {
        private static final HeartbeatService INSTANCE = new HeartbeatService();
    }

    private HeartbeatService() {
    }

    public static HeartbeatService getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void startFlushScheduleStatus() {
        ses = Executors.newScheduledThreadPool(1);
        //300秒后，每60秒心跳一次
        ses.scheduleAtFixedRate(() -> {
            try {
                logger.info("itstack middleware schedule heart beat On-Site Inspection task");
                Map<String, ScheduledTask> scheduledTasks = Constants.scheduledTasks;
                Map<String, List<ExecOrder>> execOrderMap = Constants.execOrderMap;
                Set<String> beanNameSet = execOrderMap.keySet();
                for (String beanName : beanNameSet) {
                    List<ExecOrder> execOrderList = execOrderMap.get(beanName);
                    for (ExecOrder execOrder : execOrderList) {
                        String taskId = execOrder.getBeanName() + "_" + execOrder.getMethodName();
                        ScheduledTask scheduledTask = scheduledTasks.get(taskId);
                        if (null == scheduledTask) continue;
                        boolean cancelled = scheduledTask.isCancelled();
                        // 路径拼装
                        String path_root_server_ip_clazz = StrUtil.joinStr(path_root_server_ip, LINE, "clazz", LINE, execOrder.getBeanName());
                        String path_root_server_ip_clazz_method = StrUtil.joinStr(path_root_server_ip_clazz, LINE, "method", LINE, execOrder.getMethodName(), LINE, "value");
                        // 获取现有值
                        ExecOrder oldExecOrder;
                        byte[] bytes = client.getData().forPath(path_root_server_ip_clazz_method);
                        if (null != bytes) {
                            String oldJson = new String(bytes, CHARSET_NAME);
                            oldExecOrder = JSON.parseObject(oldJson, ExecOrder.class);
                        } else {
                            oldExecOrder = new ExecOrder();
                            oldExecOrder.setBeanName(execOrder.getBeanName());
                            oldExecOrder.setMethodName(execOrder.getMethodName());
                            oldExecOrder.setDesc(execOrder.getDesc());
                            oldExecOrder.setCron(execOrder.getCron());
                            oldExecOrder.setAutoStartup(execOrder.getAutoStartup());
                        }
                        oldExecOrder.setAutoStartup(!cancelled);
                        //临时节点[数据]
                        if (null == Constants.Global.client.checkExists().forPath(path_root_server_ip_clazz_method))
                            continue;
                        String newJson = JSON.toJSONString(oldExecOrder);
                        Constants.Global.client.setData().forPath(path_root_server_ip_clazz_method, newJson.getBytes(CHARSET_NAME));
                        //永久节点[数据]
                        String path_root_ip_server_clazz_method_status = StrUtil.joinStr(path_root_server_ip_clazz, LINE, "method", LINE, execOrder.getMethodName(), "/status");
                        if (null == Constants.Global.client.checkExists().forPath(path_root_ip_server_clazz_method_status))
                            continue;
                        Constants.Global.client.setData().forPath(path_root_ip_server_clazz_method_status, (execOrder.getAutoStartup() ? "1" : "0").getBytes(CHARSET_NAME));
                    }
                }
            } catch (Exception ignore) {
            }

        }, 300, 60, TimeUnit.SECONDS);
    }

}

