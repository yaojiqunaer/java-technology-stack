package io.github.yaojiqunaer.netty.iotest.nio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/**
 * @Title
 * @Description:
 * @Create Date: 2025/08/14 01:10
 * @Author xiaodongzhang
 */
@Slf4j
public class NioServer {

    public static void main(String[] args) throws InterruptedException {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
             Selector selector = Selector.open()) {
            serverSocketChannel.bind(new InetSocketAddress(9998));
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            while (true) {
                if (selector.select(30) > 0) {
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                        if (key.isAcceptable()) {
                            handleAccept(selector, key);
                        } else if (key.isReadable()) {
                            handleRead(key);
                        } else if (key.isWritable()) {
                            handleWrite(key);
                        }
                        iterator.remove();
                    }
                }
                TimeUnit.MICROSECONDS.sleep(20);
            }
        } catch (IOException e) {
            log.error("", e);
        }
    }

    private static void handleWrite(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer attachment = (ByteBuffer) key.attachment();
        channel.write(ByteBuffer.wrap("Server: ".getBytes()));
        channel.write(attachment);
        // 读取完成，切换为写模式，清空缓冲区，继续监听读事件
        attachment.clear();
        key.interestOps(SelectionKey.OP_READ);
    }

    private static void handleRead(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer attachment = (ByteBuffer) key.attachment();
        // 从Channel读，写入Buffer
        int read = channel.read(attachment);
        if (read == -1) {
            log.info("客户端断开连接");
            channel.close();
            key.cancel();
            return;
        }
        log.info("From client: {}", new String(attachment.array(), 0, read));
        // 切换到读模式
        attachment.flip();
        // 注册写入事件
        key.interestOps(SelectionKey.OP_WRITE);
    }

    private static void handleAccept(Selector selector, SelectionKey key) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(8 * 1024));
    }
}