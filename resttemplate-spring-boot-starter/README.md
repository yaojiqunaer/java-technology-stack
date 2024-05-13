# restemplate-spring-boot-starter

🚀 通过配置注入RestTemplate

## 如何使用

1. 引入pom

````xml

<dependency>
    <groupId>io.github.yaojiqunaer</groupId>
    <artifactId>resttemplate-spring-boot-starter</artifactId>
    <version>0.0.1</versiojn>
</dependency>
````

2. 启用组件

使用注解``@EnableRestTemplate``启用starter

```java

@SpringBootApplication
@EnableRestTemplate
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class);
    }
}
```

3. 配置组件
   组件有默认的配置值，可以通过properties文件进行配置

```yaml
spring:
  component:
    rest-template:
      http-conn-pool:
        connect-timeout: 30_000
      strategy:
        follow-redirect: true
        ssl-verify: false
```

## 组件配置说明

| 配置项                                                                      | 默认值   | 说明                                                             |
|--------------------------------------------------------------------------|-------|----------------------------------------------------------------|
| spring.component.rest-template.http-conn-pool.connect-timeout            | 10000 | 连接上服务器(握手成功)的时间(毫秒)，超出抛出connect timeout异常                      |
| spring.component.rest-template.http-conn-pool.connection-request-timeout | 5000  | 从连接池中获取连接的超时时间，超时间未拿到可用连接，会抛出ConnectionRequestTimeoutException |
| spring.component.rest-template.http-conn-pool.default-max-per-route      | 30    | 同一路由并行数量                                                       |   
| spring.component.rest-template.http-conn-pool.max-total                  | 50    | Http连接池最大连接数                                                   |
| spring.component.rest-template.http-conn-pool.response-timeout           | 65000 | 服务器返回数据(response)的时间，超过抛出read timeout异常                        |
| spring.component.rest-template.http-conn-pool.validate-after-inactivity  | 2000  | 空闲永久连接检查间隔                                                     |
| spring.component.rest-template.strategy.follow-redirect                  | false | 是否跟随3XX重定向，RestTemplate默认不会                                    |
| spring.component.rest-template.strategy.ssl-verify                       | true  | 是否开启SSL校验(HTTPS)                                               |

