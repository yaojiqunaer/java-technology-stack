package io.github.yaojiqunaer.dubbo.provider.service;

import io.github.yaojiqunaer.dubbo.api.HelloService;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @Title
 * @Description:
 * @Create Date: 2025/08/11 21:54
 * @Author xiaodongzhang
 */
@DubboService
public class HelloServiceImpl implements HelloService {

    @Override
    public String hello(String name) {
        return name;
    }
}
