package io.github.yaojiqunaer.netty.iotest.aio;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

@Slf4j
public class AioServer {


    public static void main(String[] args) throws IOException, InterruptedException {
        AsynchronousServerSocketChannel asynchronousServerSocketChannel =
                AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(9996));
        asynchronousServerSocketChannel.accept(null, new CompletionHandler<>() {
            @Override
            public void completed(AsynchronousSocketChannel client, Object attachment) {
                // 监听新的连接事件，不阻塞新的连接
                try {
                    log.info("accept new connection, client: {}", client.getRemoteAddress());
                    asynchronousServerSocketChannel.accept(null, this);
                    ByteBuffer buffer = ByteBuffer.allocate(8 * 1024);
                    client.read(buffer, buffer, new ReadHandler(client));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                log.error("accept error", exc);
            }
        });
        Thread.currentThread().join();
    }

    @AllArgsConstructor
    @Getter
    private static class ReadHandler implements CompletionHandler<Integer, ByteBuffer> {

        private final AsynchronousSocketChannel asynchronousSocketChannel;

        @Override
        public void completed(Integer read, ByteBuffer attachment) {
            if (read == -1) {
                try {
                    asynchronousSocketChannel.close();
                    return;
                } catch (IOException e) {
                    log.error("error", e);
                }
            }
            ByteBuffer flip = attachment.flip();
            byte[] msg = new byte[read];
            System.arraycopy(flip.array(), 0, msg, 0, read);
            log.info("read: {}", new String(msg));
            ByteBuffer writeBuffer = ByteBuffer.wrap(("server receive: " + new String(msg)).getBytes());
            asynchronousSocketChannel.write(writeBuffer, writeBuffer, new WriteHandler(this));
        }

        @Override
        public void failed(Throwable exc, ByteBuffer attachment) {
            log.error("Read failed: {}", exc.getMessage(), exc);
        }
    }

    @AllArgsConstructor
    private static class WriteHandler implements CompletionHandler<Integer, ByteBuffer> {

        private final ReadHandler readHandler;

        @Override
        public void completed(Integer write, ByteBuffer attachment) {
            AsynchronousSocketChannel asynchronousSocketChannel = readHandler.getAsynchronousSocketChannel();
            attachment.clear();
            // 重新注册写的异步handler
            asynchronousSocketChannel.read(attachment, attachment, this.readHandler);
        }

        @Override
        public void failed(Throwable exc, ByteBuffer attachment) {
            log.error("Write failed: {}", exc.getMessage(), exc);
        }
    }

}