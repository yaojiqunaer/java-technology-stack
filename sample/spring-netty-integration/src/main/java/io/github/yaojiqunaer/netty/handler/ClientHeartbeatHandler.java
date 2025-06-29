package io.github.yaojiqunaer.netty.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 客户端心跳处理器
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class ClientHeartbeatHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            
            if (event.state() == IdleState.READER_IDLE) {
                log.debug("读空闲，可能服务器已断开连接");
                // 服务器可能已断开连接，关闭通道并重连
                ctx.close();
            } else if (event.state() == IdleState.WRITER_IDLE) {
                // 写空闲，发送心跳消息
                log.debug("写空闲，发送心跳消息");
                // 通过通道直接发送心跳消息
                try {
                    String heartbeatMessage = "{\"type\":\"HEARTBEAT\",\"content\":\"ping\",\"sender\":\"client\",\"timestamp\":" + System.currentTimeMillis() + "}";
                    ctx.writeAndFlush(heartbeatMessage);
                } catch (Exception e) {
                    log.error("发送心跳消息失败", e);
                }
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
} 