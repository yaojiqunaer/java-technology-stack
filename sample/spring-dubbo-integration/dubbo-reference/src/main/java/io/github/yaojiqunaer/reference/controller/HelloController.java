package io.github.yaojiqunaer.reference.controller;

import io.github.yaojiqunaer.api.HelloService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Title
 * @Description:
 * @Create Date: 2025/08/11 22:02
 * @Author xiaodongzhang
 */
@RestController
public class HelloController {

    @DubboReference
    private HelloService helloService;

    @GetMapping("/hello")
    public String hello(String name) {
        return helloService.hello("Hello: " + name);
    }
}
