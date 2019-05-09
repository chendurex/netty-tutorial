package com.netty.tutorial.simplist;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpRequestEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author cheny.huang
 * @date 2019-05-09 10:42.
 */
@Slf4j
public class LineBaseClient {
    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            new Bootstrap().group(group).channel(NioSocketChannel.class).handler(new ChannelInitializer() {
                @Override
                protected void initChannel(Channel ch) throws Exception {
                    ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            ctx.writeAndFlush(ctx.alloc().buffer(50).writeBytes("hello world1\nhello world2\n".getBytes()));
                            TimeUnit.SECONDS.sleep(1);
                            ctx.writeAndFlush(ctx.alloc().buffer(50).writeBytes("hello world1\nhello world2\n".getBytes()));
                            log.info("send value to server");
                        }

                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            ByteBuf buf = (ByteBuf) msg;
                            byte[] b = new byte[10];
                            buf.getBytes(0, b);
                            log.info("server response,{}", new String(b));
                        }
                    });
                }
            }).connect("localhost", 8080).sync().channel().close();
        } catch (InterruptedException e) {
            e.printStackTrace();
            group.shutdownGracefully();
        }
    }
}
