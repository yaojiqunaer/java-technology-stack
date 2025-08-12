# Spring Boot 集成 Apache Dubbo 示例

本项目演示了如何在Spring Boot应用程序中集成Apache Dubbo，实现服务提供者和服务消费者。

## 项目结构

```
spring-dubbo-integration/
├── dubbo-api/              # API模块，定义服务接口
├── dubbo-provider/         # 服务提供者模块
├── dubbo-reference/        # 服务消费者模块
└── README.md
```

### 模块说明

1. **dubbo-api**: 定义服务接口，供服务提供者实现和服务消费者调用
2. **dubbo-provider**: 实现服务接口，作为Dubbo服务提供者
3. **dubbo-reference**: 作为Dubbo服务消费者，调用远程服务

## 技术栈

- Spring Boot 3.x
- Apache Dubbo 3.3.5
- Zookeeper (注册中心)
- Maven 3.x

## 环境准备

### 1. 安装 Zookeeper

Dubbo需要一个注册中心来管理服务提供者和服务消费者，本示例使用Zookeeper作为注册中心。

可以通过以下方式安装Zookeeper：

#### 使用 Docker (推荐)

```bash
docker run -d --name zookeeper -p 2181:2181 zookeeper:3.8
```

#### 本地安装

1. 下载 [Zookeeper](https://zookeeper.apache.org/releases.html)
2. 解压并配置
3. 启动Zookeeper: `bin/zkServer.sh start`

### 2. 验证 Zookeeper

```bash
telnet localhost 2181
# 输入 "stat" 命令验证是否连接成功
```

## 快速开始

### 1. 构建项目

在项目根目录下执行：

```bash
mvn clean install
```

### 2. 启动服务提供者

进入 [dubbo-provider](dubbo-provider) 目录并启动应用：

```bash
cd dubbo-provider
mvn spring-boot:run
```

或者运行jar包：

```bash
mvn clean package
java -jar target/dubbo-provider-0.0.1.jar
```

### 3. 启动服务消费者

进入 [dubbo-reference](dubbo-reference) 目录并启动应用：

```bash
cd dubbo-reference
mvn spring-boot:run
```

或者运行jar包：

```bash
mvn clean package
java -jar target/dubbo-reference-0.0.1.jar
```

### 4. 测试服务调用

服务消费者提供了一个REST接口来调用远程服务：

```bash
curl "http://localhost:8082/hello?name=World"
```

预期返回:
```
Hello: World
```

## 配置说明

### 服务提供者配置 (dubbo-provider/src/main/resources/application.yaml)

```yaml
server:
  port: 8081
dubbo:
  application:
    name: dubbo-provider
  registry:
    address: zookeeper://localhost:2181
    username: hello
    password: 1234
    timeout: 30_000
    session: 60_000
```

### 服务消费者配置 (dubbo-reference/src/main/resources/application.yaml)

```yaml
server:
  port: 8082

dubbo:
  application:
    name: consumer-service
  registry:
    address: zookeeper://localhost:2181
    username: hello
    password: 1234
    timeout: 30_000
    session: 60_000
```

## 核心代码说明

### 1. 定义服务接口 (dubbo-api)

```java
public interface HelloService {
    String hello(String name);
}
```

### 2. 实现服务 (dubbo-provider)

```java
@DubboService
public class HelloServiceImpl implements HelloService {
    @Override
    public String hello(String name) {
        return name;
    }
}
```

### 3. 调用服务 (dubbo-reference)

```java
@RestController
public class HelloController {
    @DubboReference
    private HelloService helloService;

    @GetMapping("/hello")
    public String hello(String name) {
        return helloService.hello("Hello: " + name);
    }
}
```

## 关键注解说明

1. `@EnableDubbo`: 启用Dubbo功能，需要在Spring Boot主类上添加
2. `@DubboService`: 标记服务实现类，将其暴露为Dubbo服务
3. `@DubboReference`: 引用远程Dubbo服务

## 故障排除

### 1. Zookeeper连接问题

确保Zookeeper正在运行，并且端口(默认2181)没有被防火墙阻止。

### 2. 服务未注册

检查服务提供者的日志，确保没有启动错误，并确认在Zookeeper中能看到注册的服务。

### 3. 服务调用失败

检查服务消费者的日志，确认是否成功订阅了服务提供者。

## 扩展示例

您可以基于此示例进行扩展：

1. 添加更多的服务接口和实现
2. 配置负载均衡策略
3. 添加服务监控
4. 配置服务分组和版本
5. 使用其他注册中心如Nacos

## 参考文档

- [Apache Dubbo官方文档](https://dubbo.apache.org/)
- [Dubbo Spring Boot Starter](https://github.com/apache/dubbo-spring-boot-starter)