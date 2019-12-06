package org.itstack.middleeware.test;

import com.alibaba.fastjson.JSON;
import org.apache.curator.framework.CuratorFramework;
import org.itstack.middleware.schedule.domain.DataCollect;
import org.itstack.middleware.schedule.domain.DcsScheduleInfo;
import org.itstack.middleware.schedule.domain.Instruct;
import org.itstack.middleware.schedule.export.DcsScheduleResource;

import java.util.List;

/**
 * 博  客：http://bugstack.cn
 * 公众号：bugstack虫洞栈 | 沉淀、分享、成长，让自己和他人都能有所收获！
 * create by 付政委 on @2019
 */
public class ApiTest {

    public static void main(String[] args) throws Exception {
        Instruct instruct = new Instruct();
        instruct.setIp("10.13.152.95");
        instruct.setSchedulerServerId("itstack-demo-springboot-helloworld");
        instruct.setBeanName("demoTask");
        instruct.setMethodName("taskMethod02");
        instruct.setCron("0/3 * * * * *");
        instruct.setStatus(1);
        System.out.println(JSON.toJSONString(instruct));

        DcsScheduleResource workerArkResource = new DcsScheduleResource("127.0.0.1:2181");
        CuratorFramework client = workerArkResource.getClient();
        //client.setData().forPath("/org/itstack/middleware/schedule/exec", JSON.toJSONString(instruct).getBytes("utf-8"));

        //List<DcsScheduleInfo> res = workerArkResource.queryDcsScheduleInfoList("itstack-demo-springboot-helloworld");

        //DataCollect dataCollect = workerArkResource.queryDataCollect();

        // 递归删除节点
        client.delete().deletingChildrenIfNeeded().forPath("/org/itstack/middleware/schedule/server/itstack-demo-springboot-helloworld/ip/10.13.152.95");

    }

}
