package io.github.yaojiqunaer.netty.server;

import io.github.yaojiqunaer.netty.handler.ServerChannelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.InetSocketAddress;

/**
 * Netty服务器实现
 */
@Slf4j
public class NettyServer {

    private final int port;
    private final int bossThreads;
    private final int workerThreads;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ChannelFuture channelFuture;

    @Autowired
    private ServerChannelInitializer serverChannelInitializer;

    public NettyServer(int port, int bossThreads, int workerThreads) {
        this.port = port;
        this.bossThreads = bossThreads;
        this.workerThreads = workerThreads;
    }

    /**
     * 启动Netty服务器
     */
    public void start() {
        log.info("正在启动Netty服务器，端口: {}", port);

        bossGroup = new NioEventLoopGroup(bossThreads);
        workerGroup = new NioEventLoopGroup(workerThreads);

        try {
            ServerBootstrap bootstrap = new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(serverChannelInitializer)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            // 绑定端口并启动服务器
            channelFuture = bootstrap.bind().sync();
            log.info("Netty服务器启动成功，监听端口: {}", port);

            // 等待服务器关闭
            // channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("Netty服务器启动失败", e);
            stop();
        }
    }

    /**
     * 停止Netty服务器
     */
    public void stop() {
        log.info("正在停止Netty服务器...");

        if (channelFuture != null && channelFuture.channel() != null) {
            channelFuture.channel().close();
        }

        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }

        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }

        log.info("Netty服务器已停止");
    }
} 