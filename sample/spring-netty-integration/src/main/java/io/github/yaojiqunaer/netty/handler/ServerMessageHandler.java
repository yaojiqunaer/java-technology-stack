package io.github.yaojiqunaer.netty.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.yaojiqunaer.netty.model.Message;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务器消息处理器
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class ServerMessageHandler extends SimpleChannelInboundHandler<String> {

    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // 用于存储所有已连接的客户端Channel
    private static final ChannelGroup CHANNEL_GROUP = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    
    // 用于存储用户名与Channel的映射关系
    private static final ConcurrentHashMap<String, Channel> USER_CHANNEL_MAP = new ConcurrentHashMap<>();

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        log.info("客户端连接: {}", channel.remoteAddress());
        
        // 将新连接的客户端添加到ChannelGroup
        CHANNEL_GROUP.add(channel);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        log.info("客户端断开连接: {}", channel.remoteAddress());
        
        // 从ChannelGroup中移除断开连接的客户端
        CHANNEL_GROUP.remove(channel);
        
        // 从用户映射中移除
        USER_CHANNEL_MAP.entrySet().removeIf(entry -> entry.getValue() == channel);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        Channel channel = ctx.channel();
        log.info("收到客户端消息: {}", msg);
        
        try {
            // 将JSON字符串转换为Message对象
            Message message = objectMapper.readValue(msg, Message.class);
            
            // 根据消息类型进行处理
            switch (message.getType()) {
                case CONNECT:
                    handleConnect(channel, message);
                    break;
                case CHAT:
                    handleChat(channel, message);
                    break;
                case DISCONNECT:
                    handleDisconnect(channel, message);
                    break;
                case HEARTBEAT:
                    handleHeartbeat(channel, message);
                    break;
                default:
                    log.warn("未知的消息类型: {}", message.getType());
            }
        } catch (Exception e) {
            log.error("处理消息时发生错误", e);
            // 发送错误响应给客户端
            Message errorMessage = new Message(Message.MessageType.CHAT, "消息格式错误", "服务器", System.currentTimeMillis());
            channel.writeAndFlush(objectMapper.writeValueAsString(errorMessage));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("处理器异常", cause);
        ctx.close();
    }

    /**
     * 处理连接消息
     */
    private void handleConnect(Channel channel, Message message) throws Exception {
        String sender = message.getSender();
        log.info("用户 {} 连接", sender);
        
        // 保存用户与Channel的映射关系
        USER_CHANNEL_MAP.put(sender, channel);
        
        // 广播用户连接消息
        Message broadcastMessage = new Message(
                Message.MessageType.CHAT,
                sender + " 加入了聊天",
                "服务器",
                System.currentTimeMillis()
        );
        
        broadcast(broadcastMessage);
    }

    /**
     * 处理聊天消息
     */
    private void handleChat(Channel channel, Message message) throws Exception {
        log.info("聊天消息: {}", message.getContent());
        
        // 广播聊天消息给所有客户端
        broadcast(message);
    }

    /**
     * 处理断开连接消息
     */
    private void handleDisconnect(Channel channel, Message message) throws Exception {
        String sender = message.getSender();
        log.info("用户 {} 断开连接", sender);
        
        // 从映射中移除用户
        USER_CHANNEL_MAP.remove(sender);
        
        // 广播用户断开连接消息
        Message broadcastMessage = new Message(
                Message.MessageType.CHAT,
                sender + " 离开了聊天",
                "服务器",
                System.currentTimeMillis()
        );
        
        broadcast(broadcastMessage);
        
        // 关闭连接
        channel.close();
    }

    /**
     * 处理心跳消息
     */
    private void handleHeartbeat(Channel channel, Message message) throws Exception {
        log.debug("收到心跳消息: {}", message.getSender());
        
        // 回复心跳消息
        Message heartbeatResponse = new Message(
                Message.MessageType.HEARTBEAT,
                "pong",
                "服务器",
                System.currentTimeMillis()
        );
        
        channel.writeAndFlush(objectMapper.writeValueAsString(heartbeatResponse));
    }

    /**
     * 广播消息给所有客户端
     */
    private void broadcast(Message message) throws Exception {
        String messageJson = objectMapper.writeValueAsString(message);
        CHANNEL_GROUP.writeAndFlush(messageJson);
    }
} 