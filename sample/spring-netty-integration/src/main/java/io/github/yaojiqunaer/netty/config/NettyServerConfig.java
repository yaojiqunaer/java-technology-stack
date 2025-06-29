package io.github.yaojiqunaer.netty.config;

import io.github.yaojiqunaer.netty.server.NettyServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Netty服务器配置类
 */
@Configuration
public class NettyServerConfig {

    @Value("${netty.server.port:8888}")
    private int port;

    @Value("${netty.server.boss-threads:1}")
    private int bossThreads;

    @Value("${netty.server.worker-threads:4}")
    private int workerThreads;

    @Bean(initMethod = "start", destroyMethod = "stop")
    public NettyServer nettyServer() {
        return new NettyServer(port, bossThreads, workerThreads);
    }
} 