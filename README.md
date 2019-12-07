# schedule-spring-boot-starter 分布式任务中间件

### 1. 版本记录

|  |  版本   |    发布日期      |   备注 |
|:--------:|:---------|:---------|:---------|
| 1 | 1.0.0-RELEASE | 2019-12-07 |  基本功能实现；任务接入、分布式启停    |
| 2 | ~~1.0.1-RELEASE~~ | 2019-12-07 |  上传测试版本    |

### 2. 环境准备

1. jdk1.8 
2. StringBoot 2.x
3. 配置中心zookeeper 3.4.14 {准备好zookeeper服务，如果windows调试可以从这里下载：https://www-eu.apache.org/dist/zookeeper}
  1. 下载后解压，在bin同级路径创建文件夹data、logs
  2. 修改conf/zoo.cfg，修改配置如下；
    
	 ```xml
	 dataDir=D:\\Program Files\\apache-zookeeper-3.4.14\\data
	 dataLogDir=D:\\Program Files\\apache-zookeeper-3.4.14\\logs
	 ```

4 打包部署控制平台
  1. 下载地址：https://github.com/fuzhengwei/itstack-middleware-control.git
  2. 部署访问：http://localhost:7397

### 3. 配置POM

```xml
<dependency>
    <groupId>org.itstack.middleware</groupId>
    <artifactId>schedule-spring-boot-starter</artifactId>
    <version>1.0.0-RELEASE</version>
</dependency>
```

### 4. 引入分布式任务DcsSchedule @EnableDcsScheduling

1. 与SpringBoot的Sceduling非常像，他的注解是；@EnableScheduling，尽可能降低使用难度
2. 这个注解主要方便给我们自己的中间件一个入口，也是😏扒拉源码发现的可以这么干{我一直说好的代码都很骚气}

```java
@SpringBootApplication
@EnableDcsScheduling
public class HelloWorldApplication {

    public static void main(String[] args) {
        SpringApplication.run(HelloWorldApplication.class, args);
    }

}
```

### 5. 在任务方法上添加注解

1. 这个注解也和SpringBoot的Schedule很像，但是多了desc描述和启停初始化控制
2. cron：执行计划
3. desc：任务描述
4. autoStartup：默认启动状态
5. 如果你的任务需要参数可以通过引入service去调用获取等方式都可以

```java
@Component("demoTaskThree")
public class DemoTaskThree {
	
    @DcsScheduled(cron = "0 0 9,13 * * *", desc = "03定时任务执行测试：taskMethod01", autoStartup = false)
    public void taskMethod01() {
        System.out.println("03定时任务执行测试：taskMethod01");
    }

    @DcsScheduled(cron = "0 0/30 8-10 * * *", desc = "03定时任务执行测试：taskMethod02", autoStartup = false)
    public void taskMethod02() {
        System.out.println("03定时任务执行测试：taskMethod02");
    }

}
```

### 6. 启动验证

1. 启动SpringBoot工程即可，autoStartup = true的会自动启动任务(任务是多线程并行执行的)
2. 启动控制平台：itstack-middleware-control，访问：http://localhost:7397/ 成功界面如下；*可以开启/关闭验证了！{功能还在完善}*
   ![微信公众号：bugstack虫洞栈 & 任务列表](https://imgconvert.csdnimg.cn/aHR0cHM6Ly9yYXcuZ2l0aHVidXNlcmNvbnRlbnQuY29tL2Z1emhlbmd3ZWkvZnV6aGVuZ3dlaS5naXRodWIuaW8vbWFzdGVyL2Fzc2V0cy9pbWFnZXMvcGljLWNvbnRlbnQvMjAxOS8xMS9pdHN0YWNrLW1pZGRsZXdhcmUtc2NoZWR1bGUtcmVsZWFzZS0wMS5wbmc?x-oss-process=image/format,png)

### 7. 疑问解答
- 微信公众号：bugstack虫洞栈
- 个人微信号：monkeycode
