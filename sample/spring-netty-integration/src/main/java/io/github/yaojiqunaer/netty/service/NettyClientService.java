package io.github.yaojiqunaer.netty.service;

import io.github.yaojiqunaer.netty.model.Message;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Netty客户端服务类
 * 用于解决循环依赖问题
 */
@Slf4j
@Service
public class NettyClientService {

    // 存储收到的消息
    private final List<Message> receivedMessages = new CopyOnWriteArrayList<>();
    
    // 消息监听器
    private final List<MessageListener> messageListeners = new ArrayList<>();

    /**
     * -- GETTER --
     *  获取连接状态
     * -- SETTER --
     *  设置连接状态

     */
    // 连接状态
    @Setter
    @Getter
    private boolean connected = false;
    
    /**
     * 触发重连的回调
     * -- SETTER --
     *  设置重连回调

     */
    @Setter
    private Runnable reconnectCallback;

    /**
     * 触发重连
     */
    public void triggerReconnect() {
        if (reconnectCallback != null) {
            reconnectCallback.run();
        }
    }
    
    /**
     * 添加消息
     */
    public void addMessage(Message message) {
        receivedMessages.add(message);
        // 通知所有监听器
        for (MessageListener listener : messageListeners) {
            listener.onMessageReceived(message);
        }
    }
    
    /**
     * 获取所有消息
     */
    public List<Message> getMessages() {
        return new ArrayList<>(receivedMessages);
    }
    
    /**
     * 添加消息监听器
     */
    public void addMessageListener(MessageListener listener) {
        messageListeners.add(listener);
    }
    
    /**
     * 移除消息监听器
     */
    public void removeMessageListener(MessageListener listener) {
        messageListeners.remove(listener);
    }

    /**
     * 清除所有消息
     */
    public void clearMessages() {
        receivedMessages.clear();
    }
    
    /**
     * 消息监听器接口
     */
    public interface MessageListener {
        void onMessageReceived(Message message);
    }
} 