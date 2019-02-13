package com.netty.tutorial.http;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.function.Consumer;

/**
 * @author cheny.huang
 * @date 2019-01-23 15:20.
 */
public final class RestCallClient {
    private static final String REMOTE_URI = "http://127.0.0.1:8808/user";
    public static void main(String[] args) throws Exception {
        URI uri = new URI(REMOTE_URI);
        EventLoopGroup group = new NioEventLoopGroup();
        try {

            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(
                                    new HttpClientCodec(),
                                    new HttpObjectAggregator(8192),
                                    new RestCallClientHandler());
                        }
                    });

            Channel ch = b.connect(uri.getHost(), uri.getPort()).sync().channel();
            BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                String msg = console.readLine();
                if (msg == null) {
                    break;
                }
                DefaultFullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1,HttpMethod.GET, REMOTE_URI);
                ch.writeAndFlush(request);
            }
        } finally {
            group.shutdownGracefully();
        }
    }
}
