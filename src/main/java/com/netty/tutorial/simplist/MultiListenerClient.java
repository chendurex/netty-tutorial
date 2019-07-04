package com.netty.tutorial.simplist;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author cheny.huang
 * @date 2019-05-21 19:12.
 */
public class MultiListenerClient {
    public static void main(String[] args) throws Exception {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        try {
            new Bootstrap().group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInboundHandlerAdapter(){
                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            byte bytes[] = new byte[((ByteBuf)msg).readableBytes()];
                            ((ByteBuf)msg).getBytes(0, bytes);
                            System.out.println("receive server1 value:"+ new String(bytes));
                        }
                    })
                    .connect("localhost", 8080);
            ChannelFuture f = new Bootstrap().group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInboundHandlerAdapter(){
                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            byte bytes[] = new byte[((ByteBuf)msg).readableBytes()];
                            ((ByteBuf)msg).getBytes(0, bytes);
                            System.out.println("receive server2 value:"+ new String(bytes));
                        }
                    })
                    .connect("localhost", 8081).sync();
            f.channel().closeFuture().sync();
        } finally {
            eventLoopGroup.shutdownGracefully();
        }
    }
}
