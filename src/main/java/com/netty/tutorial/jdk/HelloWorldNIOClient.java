package com.netty.tutorial.jdk;

import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
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
        /**
         * 最好是使用 SocketChannel socketChannel = SelectorProvider.provider().openSocketChannel();
         * 而不是SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("localhost", 8080));
         * 因为SocketChannel.open()内部还是调用SelectorProvider.provider().openSocketChannel()，而且在调用
         * provider()方法时会执行同步操作，在客户端层时，创建大量的连接会存在一点没必要的性能瓶颈，应该是直接存储
         * SelectorProvider.provider()对象
         * See <a href="https://github.com/netty/netty/issues/2308">#2308</a>.
         */
        SocketChannel socketChannel = SelectorProvider.provider().openSocketChannel();
        socketChannel.connect(new InetSocketAddress("localhost", 8080));
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
