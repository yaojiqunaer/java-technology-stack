package io.github.yaojiqunaer.netty.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 通用消息模型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    /**
     * 消息类型
     */
    private MessageType type;
    
    /**
     * 消息内容
     */
    private String content;
    
    /**
     * 发送者
     */
    private String sender;
    
    /**
     * 时间戳
     */
    private long timestamp;
    
    /**
     * 消息类型枚举
     */
    public enum MessageType {
        /**
         * 连接消息
         */
        CONNECT,
        
        /**
         * 聊天消息
         */
        CHAT,
        
        /**
         * 断开连接消息
         */
        DISCONNECT,
        
        /**
         * 心跳消息
         */
        HEARTBEAT
    }
} 