package io.github.yaojiqunaer.netty.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 服务器心跳处理器
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class ServerHeartbeatHandler extends ChannelInboundHandlerAdapter {

    // 最大允许的空闲次数
    private static final int MAX_IDLE_COUNT = 3;
    
    // 用于记录每个通道的空闲次数
    private final java.util.concurrent.ConcurrentHashMap<ChannelHandlerContext, Integer> idleCountMap = 
            new java.util.concurrent.ConcurrentHashMap<>();

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            
            if (event.state() == IdleState.READER_IDLE) {
                handleReaderIdle(ctx);
            } else if (event.state() == IdleState.WRITER_IDLE) {
                handleWriterIdle(ctx);
            } else if (event.state() == IdleState.ALL_IDLE) {
                handleAllIdle(ctx);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 移除空闲计数
        idleCountMap.remove(ctx);
        super.channelInactive(ctx);
    }

    /**
     * 处理读空闲事件
     */
    private void handleReaderIdle(ChannelHandlerContext ctx) {
        log.debug("读空闲事件触发: {}", ctx.channel().remoteAddress());
        incrementIdleCount(ctx);
    }

    /**
     * 处理写空闲事件
     */
    private void handleWriterIdle(ChannelHandlerContext ctx) {
        log.debug("写空闲事件触发: {}", ctx.channel().remoteAddress());
        // 写空闲时可以发送心跳包
    }

    /**
     * 处理全部空闲事件
     */
    private void handleAllIdle(ChannelHandlerContext ctx) {
        log.debug("全部空闲事件触发: {}", ctx.channel().remoteAddress());
        incrementIdleCount(ctx);
    }

    /**
     * 增加空闲计数，如果超过最大次数则关闭连接
     */
    private void incrementIdleCount(ChannelHandlerContext ctx) {
        int count = idleCountMap.getOrDefault(ctx, 0) + 1;
        idleCountMap.put(ctx, count);
        
        if (count >= MAX_IDLE_COUNT) {
            log.warn("连接空闲超过最大次数，关闭连接: {}", ctx.channel().remoteAddress());
            ctx.close();
            idleCountMap.remove(ctx);
        }
    }
} 