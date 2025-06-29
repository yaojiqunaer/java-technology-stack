package io.github.yaojiqunaer.netty.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.yaojiqunaer.netty.model.Message;
import io.github.yaojiqunaer.netty.service.NettyClientService;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 客户端消息处理器
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class ClientMessageHandler extends SimpleChannelInboundHandler<String> {

    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Autowired
    private NettyClientService clientService;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("连接到服务器: {}", ctx.channel().remoteAddress());
        clientService.setConnected(true);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.info("与服务器断开连接: {}", ctx.channel().remoteAddress());
        clientService.setConnected(false);
        
        // 触发重连
        clientService.triggerReconnect();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        log.info("收到服务器消息: {}", msg);
        
        try {
            // 将JSON字符串转换为Message对象
            Message message = objectMapper.readValue(msg, Message.class);
            
            // 添加到消息服务
            clientService.addMessage(message);
            
            // 根据消息类型进行处理
            switch (message.getType()) {
                case CHAT:
                    handleChatMessage(message);
                    break;
                case HEARTBEAT:
                    handleHeartbeatMessage(message);
                    break;
                default:
                    log.debug("收到其他类型消息: {}", message.getType());
            }
        } catch (Exception e) {
            log.error("处理消息时发生错误", e);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("处理器异常", cause);
        ctx.close();
    }

    /**
     * 处理聊天消息
     */
    private void handleChatMessage(Message message) {
        log.info("[{}] {}: {}", 
                new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date(message.getTimestamp())),
                message.getSender(), 
                message.getContent());
    }

    /**
     * 处理心跳消息
     */
    private void handleHeartbeatMessage(Message message) {
        log.debug("收到心跳响应: {}", message.getContent());
    }
} 