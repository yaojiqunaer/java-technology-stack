# exception-spring-boot-starter

🚀 通过配置注入全员异常拦截器

## 如何使用

1. 引入pom

````xml

<dependency>
    <groupId>io.github.yaojiqunaer</groupId>
    <artifactId>exception-spring-boot-starter</artifactId>
    <version>0.0.1</versiojn>
</dependency>
````

2. 启用组件

使用注解``@EnableGlobalException``启用starter

```java

@SpringBootApplication
@EnableGlobalException
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class);
    }
}
```
