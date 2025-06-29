package io.github.yaojiqunaer.netty.handler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.json.JsonObjectDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 客户端通道初始化器
 */
@Component
public class ClientChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Autowired
    private ClientMessageHandler clientMessageHandler;
    
    @Autowired
    private ClientHeartbeatHandler clientHeartbeatHandler;

    @Override
    protected void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        
        // 添加心跳检测处理器，读空闲60秒，写空闲30秒，全部空闲0秒（禁用）
        pipeline.addLast(new IdleStateHandler(60, 30, 0, TimeUnit.SECONDS));
        
        // 添加JSON解码器
        pipeline.addLast(new JsonObjectDecoder());
        
        // 添加字符串编解码器
        pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
        pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
        
        // 添加心跳处理器
        pipeline.addLast(clientHeartbeatHandler);
        
        // 添加消息处理器
        pipeline.addLast(clientMessageHandler);
    }
} 