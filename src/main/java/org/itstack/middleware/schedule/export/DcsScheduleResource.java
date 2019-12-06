package org.itstack.middleware.schedule.export;

import com.alibaba.fastjson.JSON;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.itstack.middleware.schedule.common.Constants;
import org.itstack.middleware.schedule.domain.*;
import org.itstack.middleware.schedule.util.StrUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.apache.zookeeper.ZooDefs.OpCode.setData;
import static org.itstack.middleware.schedule.common.Constants.Global.*;
import static org.itstack.middleware.schedule.common.Constants.Global.schedulerServerId;

/**
 * 博  客：http://bugstack.cn
 * 公众号：bugstack虫洞栈 | 沉淀、分享、成长，让自己和他人都能有所收获！
 * create by 付政委 on @2019
 */
public class DcsScheduleResource {

    private CuratorFramework client;

    private DcsScheduleResource() {
    }

    public DcsScheduleResource(String waAddress) {
        client = CuratorFrameworkFactory.newClient(waAddress, new RetryNTimes(10, 5000));
        client.start();
    }

    public CuratorFramework getClient() {
        return client;
    }

    private List<String> getChildren(String path) throws Exception {
        return client.getChildren().forPath(path);
    }

    private int getChildrenCount(String path) throws Exception {
        return client.getChildren().forPath(path).size();
    }

    private String getDate(String path) throws Exception {
        byte[] bytes = client.getData().forPath(path);
        if (null == bytes || bytes.length <= 0) return null;
        return new String(bytes, CHARSET_NAME);
    }

    private void setData(String path, Object value) throws Exception {
        if (null == client.checkExists().forPath(path)) return;
        client.setData().forPath(path, JSON.toJSONString(value).getBytes(CHARSET_NAME));
    }

    public List<String> queryPathRootServerList() throws Exception {
        return getChildren(StrUtil.joinStr(path_root, Constants.Global.LINE, "server"));
    }

    private List<String> queryPathRootServerIpList(String schedulerServerId) throws Exception {
        return getChildren(StrUtil.joinStr(path_root, Constants.Global.LINE, "server", Constants.Global.LINE, schedulerServerId, Constants.Global.LINE, "ip"));
    }

    private List<String> queryPathRootServerIpClazz(String schedulerServerId, String ip) throws Exception {
        return getChildren(StrUtil.joinStr(path_root, Constants.Global.LINE, "server", Constants.Global.LINE, schedulerServerId, Constants.Global.LINE, "ip", LINE, ip, LINE, "clazz"));
    }

    private List<String> queryPathRootServerIpClazzMethod(String schedulerServerId, String ip, String clazz) throws Exception {
        return getChildren(StrUtil.joinStr(path_root, Constants.Global.LINE, "server", Constants.Global.LINE, schedulerServerId, Constants.Global.LINE, "ip", LINE, ip, LINE, "clazz", LINE, clazz, LINE, "method"));
    }

    private ExecOrder queryExecOrder(String schedulerServerId, String ip, String clazz, String method) throws Exception {
        String path = StrUtil.joinStr(path_root, Constants.Global.LINE, "server", Constants.Global.LINE, schedulerServerId, Constants.Global.LINE, "ip", LINE, ip, LINE, "clazz", LINE, clazz, LINE, "method", LINE, method, LINE, "value");
        if (null == client.checkExists().forPath(path)) return null;
        String objJson = getDate(path);
        if (null == objJson) return null;
        return JSON.parseObject(objJson, ExecOrder.class);
    }

    private boolean queryStatus(String schedulerServerId, String ip, String clazz, String method) throws Exception {
        String path = StrUtil.joinStr(path_root, Constants.Global.LINE, "server", Constants.Global.LINE, schedulerServerId, Constants.Global.LINE, "ip", LINE, ip, LINE, "clazz", LINE, clazz, LINE, "method", LINE, method, LINE, "status");
        String statusStr = getDate(path);
        return "1".equals(statusStr);
    }

    public List<DcsScheduleInfo> queryDcsScheduleInfoList(String schedulerServerId) throws Exception {
        List<DcsScheduleInfo> dcsScheduleInfoList = new ArrayList<>();
        String path_root_server = StrUtil.joinStr(path_root, Constants.Global.LINE, "server", LINE, schedulerServerId);
        String schedulerServerName = getDate(path_root_server);
        //查询封装结果集
        List<String> ipList = queryPathRootServerIpList(schedulerServerId);
        for (String ip : ipList) {
            List<String> clazzList = queryPathRootServerIpClazz(schedulerServerId, ip);
            for (String clazz : clazzList) {
                List<String> methodList = queryPathRootServerIpClazzMethod(schedulerServerId, ip, clazz);
                for (String method : methodList) {
                    ExecOrder execOrder = queryExecOrder(schedulerServerId, ip, clazz, method);
                    //封装对象
                    DcsScheduleInfo dcsScheduleInfo = new DcsScheduleInfo();
                    dcsScheduleInfo.setIp(ip);
                    dcsScheduleInfo.setSchedulerServerId(schedulerServerId);
                    dcsScheduleInfo.setSchedulerServerName(schedulerServerName);
                    dcsScheduleInfo.setBeanName(clazz);
                    dcsScheduleInfo.setMethodName(method);
                    if (null != execOrder) {
                        dcsScheduleInfo.setDesc(execOrder.getDesc());
                        dcsScheduleInfo.setCron(execOrder.getCron());
                        dcsScheduleInfo.setStatus(queryStatus(schedulerServerId, ip, clazz, method) ? 1 : 0);
                    } else {
                        dcsScheduleInfo.setStatus(2);
                    }
                    dcsScheduleInfoList.add(dcsScheduleInfo);
                }
            }
        }
        return dcsScheduleInfoList;
    }

    public DataCollect queryDataCollect() throws Exception {
        List<String> serverList = queryPathRootServerList();
        AtomicInteger ipCount = new AtomicInteger(0), serverCount = new AtomicInteger(serverList.size()), beanCount = new AtomicInteger(0), methodCount = new AtomicInteger(0);
        for (String schedulerServerId : serverList) {
            List<String> ipList = queryPathRootServerIpList(schedulerServerId);
            ipCount.getAndAdd(ipList.size());
            for (String ip : ipList) {
                List<String> clazzList = queryPathRootServerIpClazz(schedulerServerId, ip);
                beanCount.addAndGet(clazzList.size());
                for (String clazz : clazzList) {
                    List<String> methodList = queryPathRootServerIpClazzMethod(schedulerServerId, ip, clazz);
                    methodCount.addAndGet(methodList.size());
                }
            }
        }
        return new DataCollect(ipCount.get(), serverCount.get(), beanCount.get(), methodCount.get());
    }

    public List<DcsServerNode> queryDcsServerNodeList() throws Exception {
        List<DcsServerNode> dcsServerNodeList = new ArrayList<>();
        List<String> serverList = queryPathRootServerList();
        for (String schedulerServerId : serverList) {
            String path = StrUtil.joinStr(path_root, LINE, "server", LINE, schedulerServerId);
            String schedulerServerName = getDate(path);
            DcsServerNode dcsServerNode = new DcsServerNode(schedulerServerId,schedulerServerName);
            dcsServerNodeList.add(dcsServerNode);
        }
        return dcsServerNodeList;
    }

    public void pushInstruct(Instruct instruct) throws Exception {
        setData("/org/itstack/middleware/schedule/exec", instruct);
    }

}
