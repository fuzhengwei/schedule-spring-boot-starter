package org.itstack.middleware.schedule.task;

import org.itstack.middleware.schedule.domain.ExecOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * 博  客：http://bugstack.cn
 * 公众号：bugstack虫洞栈 | 沉淀、分享、成长，让自己和他人都能有所收获！
 * create by 付政委 on 2019/12/4
 */
public class SchedulingRunnable implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(SchedulingRunnable.class);

    private Object bean;         //类对象
    private String beanName;     //类名称
    private String methodName;   //方法名称

    public SchedulingRunnable(Object bean, String beanName, String methodName) {
        this.bean = bean;
        this.beanName = beanName;
        this.methodName = methodName;
    }

    @Override
    public void run() {
        try {
            Method method = bean.getClass().getDeclaredMethod(methodName);
            ReflectionUtils.makeAccessible(method);
            method.invoke(bean);
        } catch (Exception e) {
            logger.error("itstack middleware schedule err！", e);
        }
    }

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        SchedulingRunnable that = (SchedulingRunnable) o;
//
//        return beanName.equals(that.beanName) &&
//                methodName.equals(that.methodName);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(beanName, methodName);
//    }

    public String taskId() {
        return beanName + "_" + methodName;
    }

}
