package io.github.yaojiqunaer.netty.controller;

import io.github.yaojiqunaer.netty.client.NettyClient;
import io.github.yaojiqunaer.netty.model.Message;
import io.github.yaojiqunaer.netty.service.NettyClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 聊天控制器
 */
@Slf4j
@RestController
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private NettyClient nettyClient;
    
    @Autowired
    private NettyClientService clientService;

    /**
     * 发送消息
     */
    @PostMapping("/send")
    public Map<String, Object> sendMessage(@RequestBody Map<String, String> payload) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String message = payload.get("message");
            String sender = payload.getOrDefault("sender", "REST-API");
            
            if (message == null || message.trim().isEmpty()) {
                result.put("success", false);
                result.put("error", "消息内容不能为空");
                return result;
            }
            
            // 创建消息对象
            Message chatMessage = new Message(
                Message.MessageType.CHAT,
                message,
                sender,
                System.currentTimeMillis()
            );
            
            // 添加到客户端服务
            clientService.addMessage(chatMessage);
            
            // 发送消息
            nettyClient.sendChatMessage(message);
            
            result.put("success", true);
            result.put("message", "消息发送成功");
        } catch (Exception e) {
            log.error("发送消息失败", e);
            result.put("success", false);
            result.put("error", "发送消息失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 检查连接状态
     */
    @GetMapping("/status")
    public Map<String, Object> getStatus() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", clientService.isConnected() ? "connected" : "disconnected");
        return result;
    }
    
    /**
     * 获取消息历史
     */
    @GetMapping("/history")
    public List<Message> getHistory() {
        return clientService.getMessages();
    }

    /**
     * 重新连接
     */
    @PostMapping("/reconnect")
    public Map<String, Object> reconnect() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            nettyClient.reconnect();
            result.put("success", true);
            result.put("message", "正在重新连接...");
        } catch (Exception e) {
            log.error("重新连接失败", e);
            result.put("success", false);
            result.put("error", "重新连接失败: " + e.getMessage());
        }
        
        return result;
    }
} 