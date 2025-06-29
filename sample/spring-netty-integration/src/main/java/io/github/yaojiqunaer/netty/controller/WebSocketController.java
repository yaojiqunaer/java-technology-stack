package io.github.yaojiqunaer.netty.controller;

import io.github.yaojiqunaer.netty.model.Message;
import io.github.yaojiqunaer.netty.service.NettyClientService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket控制器
 */
@Slf4j
@Controller
public class WebSocketController {

    @Autowired
    private NettyClientService clientService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @PostConstruct
    public void init() {
        // 添加消息监听器，将Netty消息转发到WebSocket
        clientService.addMessageListener(message -> {
            if (message.getType() == Message.MessageType.CHAT) {
                messagingTemplate.convertAndSend("topic/messages", message);
            }
        });
    }

    /**
     * 处理发送消息请求
     */
    @MessageMapping("send")
    @SendTo("topic/messages")
    public Message sendMessage(Message message) {
        try {
            // 设置时间戳
            message.setTimestamp(System.currentTimeMillis());

            // 通过Netty客户端发送消息
            clientService.addMessage(message);

            return message;
        } catch (Exception e) {
            log.error("发送消息失败", e);
            return new Message(Message.MessageType.CHAT, "发送失败: " + e.getMessage(), "系统", System.currentTimeMillis());
        }
    }

    /**
     * WebSocket配置
     */
    @Configuration
    @EnableWebSocketMessageBroker
    public static class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

        @Override
        public void configureMessageBroker(MessageBrokerRegistry config) {
            config.enableSimpleBroker("topic");
            config.setApplicationDestinationPrefixes("app");
        }

        @Override
        public void registerStompEndpoints(StompEndpointRegistry registry) {
            registry.addEndpoint("/ws")
                    .setAllowedOriginPatterns("*")
                    .withSockJS();
        }
    }
} 