# Spring Netty 集成示例

这是一个Spring Boot与Netty框架集成的示例项目，展示了如何在Spring Boot应用中使用Netty实现高性能的网络通信。

## 功能特性

- 基于Spring Boot 3.x和Netty 4.x
- 实现了一个简单的聊天服务器和客户端
- 支持JSON格式的消息交换
- 实现了心跳检测机制
- 客户端自动重连功能
- 提供REST API接口进行消息发送
- 基于WebSocket的实时聊天页面
- 消息历史记录功能

## 项目结构

```
src/main/java/io/github/yaojiqunaer/netty/
├── NettyApplication.java               # 应用程序入口
├── config/                             # 配置类
│   ├── NettyServerConfig.java          # 服务器配置
│   └── NettyClientConfig.java          # 客户端配置
├── server/                             # 服务器实现
│   └── NettyServer.java                # Netty服务器
├── client/                             # 客户端实现
│   └── NettyClient.java                # Netty客户端
├── handler/                            # 处理器
│   ├── ServerChannelInitializer.java   # 服务器通道初始化器
│   ├── ServerMessageHandler.java       # 服务器消息处理器
│   ├── ServerHeartbeatHandler.java     # 服务器心跳处理器
│   ├── ClientChannelInitializer.java   # 客户端通道初始化器
│   ├── ClientMessageHandler.java       # 客户端消息处理器
│   └── ClientHeartbeatHandler.java     # 客户端心跳处理器
├── model/                              # 数据模型
│   └── Message.java                    # 消息模型
├── service/                            # 服务类
│   └── NettyClientService.java         # 客户端服务类
└── controller/                         # 控制器
    ├── ChatController.java             # REST聊天控制器
    ├── WebSocketController.java        # WebSocket控制器
    └── PageController.java             # 页面控制器

src/main/resources/
└── static/
    └── index.html                      # 聊天页面
```

## 如何使用

1. 启动应用程序：
   ```
   mvn spring-boot:run
   ```

2. 应用启动后，会同时启动Netty服务器和客户端：
   - Netty服务器监听端口：8888
   - Spring Boot Web服务监听端口：8080

3. 访问聊天页面：
   - 浏览器访问：http://localhost:8080/netty-demo/chat
   - 输入昵称和消息进行聊天

4. 通过REST API发送消息：
   ```
   curl -X POST http://localhost:8080/netty-demo/chat/send \
     -H "Content-Type: application/json" \
     -d '{"message": "Hello, Netty!"}'
   ```

5. 查看连接状态：
   ```
   curl http://localhost:8080/netty-demo/chat/status
   ```

6. 获取消息历史：
   ```
   curl http://localhost:8080/netty-demo/chat/history
   ```

7. 手动触发重连：
   ```
   curl -X POST http://localhost:8080/netty-demo/chat/reconnect
   ```

## 技术栈

- Spring Boot 3.x
- Netty 4.x
- WebSocket (STOMP)
- Jackson (JSON处理)
- Bootstrap 5 (前端UI)
- jQuery (前端交互)
- Lombok

## 消息类型

系统支持以下消息类型：

1. **连接消息(CONNECT)**: 客户端连接到服务器时发送
2. **聊天消息(CHAT)**: 用于发送聊天内容
3. **断开连接消息(DISCONNECT)**: 客户端断开连接时发送
4. **心跳消息(HEARTBEAT)**: 用于保持连接活跃

## 心跳机制

- 客户端每30秒发送一次心跳消息
- 服务器60秒内未收到任何消息则认为客户端断开
- 客户端60秒内未收到任何消息则认为服务器断开并尝试重连

## 注意事项

- 本示例同时运行了服务器和客户端，实际应用中可能需要分离
- 生产环境中应考虑更完善的错误处理和重试机制
- 消息格式可以根据实际需求进行扩展 