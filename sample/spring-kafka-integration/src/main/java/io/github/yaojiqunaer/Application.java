package io.github.yaojiqunaer;

import io.github.yaojiqunaer.exception.annotation.EnableGlobalException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableGlobalException
public class Application {
//https://www.cnblogs.com/xfeiyun/p/16185664.html

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}