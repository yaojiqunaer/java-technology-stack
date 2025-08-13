package io.github.yaojiqunaer.netty.iotest.bio;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * @Title
 * @Description:
 * @Create Date: 2025/08/13 23:27
 * @Author xiaodongzhang
 */
@Slf4j
public class BioServer {

    public static final int SERVER_PORT = 9999;

    public static void main(String[] args) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            do {
                log.info("waiting for a new connection...");
                Socket socket = serverSocket.accept();
                log.info("accept a new connection: {}, current time: {}", socket.getInetAddress(),
                        LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
                handle(socket);
            } while (new Random().nextInt(100_000_000) != 1);
        }
    }

    private static void handle(Socket socket) {
        new Thread(() -> {
            try (socket;
                 BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter pw = new PrintWriter(socket.getOutputStream(), true)) {
                String readLine;
                while ((readLine = bufferedReader.readLine()) != null) {
                    Thread.sleep(20);
                    log.info("client {} send: {}", socket.getInetAddress(), readLine);
                    pw.println("server receive: " + readLine);
                }
            } catch (Exception e) {
                log.error("handle socket error", e);
            }
            log.info("client {} disconnected", socket.getInetAddress());
        }).start();
    }

}
