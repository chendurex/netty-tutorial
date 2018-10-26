package com.netty.tutorial.jdk;

import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author cheny.huang
 * @date 2018-10-26 16:24.
 */
@Slf4j
public class HelloWorldNIOClient {
    public static void main(String[] args) throws Exception {
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("localhost", 8080));
        log.info("connection remote server on port 8080...");
        List<String> v = Arrays.asList("hello", "world", "bye");
        for (String s : v) {
            ByteBuffer byteBuffer = ByteBuffer.wrap(s.getBytes());
            socketChannel.write(byteBuffer);
            byteBuffer.clear();
            log.info("sending ...{}", s);
            TimeUnit.SECONDS.sleep(2);
            ByteBuffer readBuf = ByteBuffer.allocate(256);
            socketChannel.read(readBuf);
            log.info("get server reply...{}", new String(readBuf.array()));
        }
        socketChannel.close();
    }
}
