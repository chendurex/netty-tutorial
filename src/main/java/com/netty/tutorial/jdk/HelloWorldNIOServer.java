package com.netty.tutorial.jdk;

import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.Set;

/**
 * @author cheny.huang
 * @date 2018-10-26 15:48.
 */
@Slf4j
public class HelloWorldNIOServer {
    public static void main(String[] args) throws Exception {
        ServerSocketChannel serverSocketChannel = SelectorProvider.provider().openServerSocketChannel();
        serverSocketChannel.bind(new InetSocketAddress("localhost", 8080));
        serverSocketChannel.configureBlocking(false);
        // 返回服务端支持的操作，因为服务端仅支持ACCEPT操作，所以也可以直接在注册的时候使用SelectionKey.OP_ACCEPT
        int ops = serverSocketChannel.validOps();
        // 开启多路复用选择器，并且注册到serverSocket中
        Selector selector = SelectorProvider.provider().openSelector();
        serverSocketChannel.register(selector, ops, null);
        log.info("i'm a server and waiting for new connection and buffer select...");
        for(;;) {
            // 开始阻塞等待接收自己感兴趣的IO操作
            selector.select();
            Set<SelectionKey> keys = selector.selectedKeys();
            for (Iterator<SelectionKey> ite = keys.iterator(); ite.hasNext();) {
                SelectionKey key = ite.next();
                ite.remove();
                if (key.isAcceptable()) {
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    socketChannel.configureBlocking(false);
                    socketChannel.register(selector, SelectionKey.OP_READ);
                    log.info("connection accepted:{}", socketChannel.getLocalAddress());
                } else if (key.isReadable()) {
                    SocketChannel socketChannel = (SocketChannel)key.channel();
                    ByteBuffer byteBuffer = ByteBuffer.allocate(256);
                    socketChannel.read(byteBuffer);
                    // key.interestOps(SelectionKey.OP_WRITE); 注册写事件
                    String result = new String(byteBuffer.array()).trim();
                    log.info("message received:{}", result);
                    socketChannel.write(ByteBuffer.wrap(result.getBytes()));
                    // 如果对等方直接close，而服务方并未close，就非常容易引发空轮训，netty已经解决这个问题了
                    if ("bye".equals(result)) {
                        socketChannel.close();
                        log.info("it's time to close connection");
                        log.info("server will keep running. try running client again to establish new connection");
                    }
                }
                // 注册写事件，这个写事件并不是代表channel.write就触发，而是代表channel是否可写，因为socket首先将数据写入缓冲，
                // 当缓冲满时，是无法写入数据的，这个时候就有可能需要注册write事件来触发写事件了，当触发完毕后记得关闭，防止
                // 缓冲空闲时一直触发，一般情况缓冲都是空闲的，所以没有必要注册写事件
                /*
                  else if (key.isWritable()) {
                    log.info("reply...");
                    SocketChannel socketChannel = (SocketChannel)key.channel();
                    socketChannel.write(ByteBuffer.wrap("server reply".getBytes()));
                }*/
            }
        }
    }
}
