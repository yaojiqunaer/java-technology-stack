package io.github.yaojiqunaer.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyApplication {

    private static final int DEFAULT_PORT = 9995;

    public static void main(String[] args) {
        // 1. 创建事件循环组（使用MultiThreadIoEventLoopGroup替代NioEventLoopGroup）
        EventLoopGroup eventLoopGroup = new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());
        try {
            // 2. 配置服务器引导程序
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(eventLoopGroup) // 单线程组模型（同时处理连接和I/O）
                    .channel(NioServerSocketChannel.class) // NIO模式
                    .option(ChannelOption.SO_BACKLOG, 1024)  // 设置TCP缓冲区
                    .childOption(ChannelOption.SO_KEEPALIVE, true)  // 设置TCP长连接
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new StringDecoder());   // 字节→字符串解码
                            pipeline.addLast(new StringEncoder());   // 字符串→字节编码
                            pipeline.addLast(new ServerHandler());   // 业务处理器
                        }
                    });
            // 3. 绑定端口并启动
            ChannelFuture future = bootstrap.bind(DEFAULT_PORT).sync();
            log.info("✅ 服务器启动成功，监听端口: {}", DEFAULT_PORT);
            // 4. 等待服务器通道关闭
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("❌ 服务器被中断: {}", e.getMessage());
        } finally {
            // 5. 优雅关闭线程池
            eventLoopGroup.shutdownGracefully();
            log.warn("⚠️ 事件循环组资源已释放");
        }
    }

    // 自定义业务处理器
    private static class ServerHandler extends SimpleChannelInboundHandler<String> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, String msg) {
            log.info("📩 收到消息: {}", msg);
            ctx.writeAndFlush("【ECHO】: " + msg); // 回显消息
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            log.error("❌ 客户端异常: {}", cause.getMessage());
            ctx.close();
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) {
            log.info("🔗 新连接: {}", ctx.channel().remoteAddress());
        }
    }
}