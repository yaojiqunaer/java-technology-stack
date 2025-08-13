package io.github.yaojiqunaer.netty.iotest.bio;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.*;
import java.net.Socket;

/**
 * @Title
 * @Description:
 * @Create Date: 2025/08/13 23:47
 * @Author xiaodongzhang
 */
@Slf4j
public class BioClient {

    public static void main(String[] args) throws IOException {
        try (Socket socket = new Socket("127.0.0.1", BioServer.SERVER_PORT);
             PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            printWriter.println("hello server, i'm " + RandomStringUtils.secure().nextGraph(10));
            String readLine = bufferedReader.readLine();
            log.info("server response: {}", readLine);
        }
    }

}
