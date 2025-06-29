package io.github.yaojiqunaer.netty.config;

import io.github.yaojiqunaer.netty.client.NettyClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Netty客户端配置类
 */
@Configuration
public class NettyClientConfig {

    @Value("${netty.client.host:localhost}")
    private String host;

    @Value("${netty.client.port:8888}")
    private int port;

    @Value("${netty.client.username:client-user}")
    private String username;

    @Bean(initMethod = "start", destroyMethod = "stop")
    public NettyClient nettyClient() {
        return new NettyClient(host, port, username);
    }
} 