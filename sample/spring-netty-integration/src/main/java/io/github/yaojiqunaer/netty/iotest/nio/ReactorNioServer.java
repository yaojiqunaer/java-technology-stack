package io.github.yaojiqunaer.netty.iotest.nio;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ReactorNioServer {

    private static final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
            2,
            50,
            60,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(500),
            new ThreadPoolExecutor.DiscardPolicy()
    );

    public static void main(String[] args) throws IOException, InterruptedException {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
             Selector selector = Selector.open()) {
            serverSocketChannel.bind(new InetSocketAddress(9997))
                    .configureBlocking(false)
                    .register(selector, SelectionKey.OP_ACCEPT);
            while (RandomUtils.secure().randomInt(0, 1_000_000) != -1) {
                if (selector.select(30) > 0) {
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                        if (key.isAcceptable()) {
                            handleAccept(selector, key);
                        }
                        if (key.isReadable()) {
                            threadPoolExecutor.execute(() -> {
                                try {
                                    handleRead(key);
                                } catch (IOException e) {
                                    closeConnection(key);
                                }
                            });
                        }
                        iterator.remove();
                    }
                }
            }
        }
    }

    private static void handleRead(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        ByteBuffer attachment = (ByteBuffer) key.attachment();
        attachment.clear();
        int read = clientChannel.read(attachment);
        if (read == -1) {
            // 客户端已断开连接
            closeConnection(key);
            return;
        }
        if (read > 0) {
            attachment.flip();
            log.info("From client: {}", new String(attachment.array(), 0, read));
            clientChannel.write(ByteBuffer.wrap("Server: ".getBytes()));
            clientChannel.write(attachment);
            attachment.clear();
        }
    }

    private static void handleAccept(Selector selector, SelectionKey key) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(8 * 1024));
    }

    private static void closeConnection(SelectionKey key) {
        try {
            SocketChannel clientChannel = (SocketChannel) key.channel();
            clientChannel.close();
            key.cancel();
        } catch (IOException e) {
            log.error("close connection error: {}", e.getMessage(), e);
        }
    }
}