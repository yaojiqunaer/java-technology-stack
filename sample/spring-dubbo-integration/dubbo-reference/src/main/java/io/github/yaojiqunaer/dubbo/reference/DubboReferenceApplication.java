package io.github.yaojiqunaer.dubbo.reference;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Title
 * @Description:
 * @Create Date: 2025/08/11 22:01
 * @Author xiaodongzhang
 */
@SpringBootApplication
@EnableDubbo
public class DubboReferenceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DubboReferenceApplication.class, args);
    }
}
