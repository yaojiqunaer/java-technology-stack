package io.github.yaojiqunaer.netty.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.yaojiqunaer.netty.handler.ClientChannelInitializer;
import io.github.yaojiqunaer.netty.model.Message;
import io.github.yaojiqunaer.netty.service.NettyClientService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Netty客户端实现
 */
@Slf4j
@Component
public class NettyClient {

    private final String host;
    private final int port;
    private final String username;
    
    private EventLoopGroup group;
    private Channel channel;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Autowired
    private ClientChannelInitializer clientChannelInitializer;
    
    @Autowired
    private NettyClientService clientService;
    
    // 重连延迟（秒）
    private final int RECONNECT_DELAY = 5;
    
    // 是否正在重连
    private boolean isReconnecting = false;
    
    @PostConstruct
    public void init() {
        // 设置重连回调
        clientService.setReconnectCallback(this::reconnect);
    }
    
    public NettyClient() {
        // 默认配置
        this("localhost", 8888, "default-user");
    }
    
    public NettyClient(String host, int port, String username) {
        this.host = host;
        this.port = port;
        this.username = username;
    }
    
    /**
     * 启动客户端
     */
    public void start() {
        log.info("正在启动Netty客户端，连接到 {}:{}", host, port);
        
        group = new NioEventLoopGroup();
        
        try {
            Bootstrap bootstrap = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .handler(clientChannelInitializer);
            
            // 连接服务器
            ChannelFuture future = bootstrap.connect(host, port).sync();
            channel = future.channel();
            
            // 发送连接消息
            sendConnectMessage();
            
            log.info("Netty客户端启动成功，已连接到 {}:{}", host, port);
            
            // 等待连接关闭
            // channel.closeFuture().sync();
        } catch (Exception e) {
            log.error("Netty客户端启动失败", e);
            reconnect();
        }
    }
    
    /**
     * 停止客户端
     */
    public void stop() {
        log.info("正在停止Netty客户端...");
        
        try {
            // 发送断开连接消息
            sendDisconnectMessage();
            
            if (channel != null) {
                channel.close();
            }
        } catch (Exception e) {
            log.error("发送断开连接消息失败", e);
        } finally {
            if (group != null) {
                group.shutdownGracefully();
            }
        }
        
        log.info("Netty客户端已停止");
    }
    
    /**
     * 重连
     */
    public void reconnect() {
        if (isReconnecting) {
            return;
        }
        
        isReconnecting = true;
        
        log.info("{}秒后尝试重新连接...", RECONNECT_DELAY);
        
        // 创建定时任务进行重连
        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(RECONNECT_DELAY);
                log.info("正在重新连接...");
                start();
                isReconnecting = false;
            } catch (InterruptedException e) {
                log.error("重连被中断", e);
                isReconnecting = false;
            }
        }).start();
    }
    
    /**
     * 发送连接消息
     */
    private void sendConnectMessage() throws Exception {
        if (channel != null && channel.isActive()) {
            Message connectMessage = new Message(
                    Message.MessageType.CONNECT,
                    "连接到服务器",
                    username,
                    System.currentTimeMillis()
            );
            
            channel.writeAndFlush(objectMapper.writeValueAsString(connectMessage));
        }
    }
    
    /**
     * 发送断开连接消息
     */
    private void sendDisconnectMessage() throws Exception {
        if (channel != null && channel.isActive()) {
            Message disconnectMessage = new Message(
                    Message.MessageType.DISCONNECT,
                    "断开连接",
                    username,
                    System.currentTimeMillis()
            );
            
            channel.writeAndFlush(objectMapper.writeValueAsString(disconnectMessage));
        }
    }
    
    /**
     * 发送聊天消息
     */
    public void sendChatMessage(String content) throws Exception {
        if (channel != null && channel.isActive()) {
            Message chatMessage = new Message(
                    Message.MessageType.CHAT,
                    content,
                    username,
                    System.currentTimeMillis()
            );
            
            // 发送消息到服务器
            channel.writeAndFlush(objectMapper.writeValueAsString(chatMessage));
            
            // 添加到客户端服务，以便在UI中显示
            clientService.addMessage(chatMessage);
        } else {
            log.error("无法发送消息，客户端未连接");
            reconnect();
        }
    }
    
    /**
     * 发送心跳消息
     */
    public void sendHeartbeat() throws Exception {
        if (channel != null && channel.isActive()) {
            Message heartbeatMessage = new Message(
                    Message.MessageType.HEARTBEAT,
                    "ping",
                    username,
                    System.currentTimeMillis()
            );
            
            channel.writeAndFlush(objectMapper.writeValueAsString(heartbeatMessage));
        }
    }
} 