package org.itstack.middleware.schedule.util;

/**
 * 博  客：http://bugstack.cn
 * 公众号：bugstack虫洞栈 | 沉淀、分享、成长，让自己和他人都能有所收获！
 * create by 付政委 on @2019
 */
public class StrUtil {

    public static String joinStr(String... str) {
        StringBuilder sb = new StringBuilder();
        for (String sign : str) {
            if (null != sign)
                sb.append(sign);
        }
        return sb.toString();
    }

}
