package com.netty.tutorial.simplist;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author cheny.huang
 * @date 2019-05-21 19:07.
 */
public class MultiListenerServer {
    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup work = new NioEventLoopGroup(10);
        new ServerBootstrap().group(boss, work).channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline().addLast("myOutboundHandlerAdapter2", new ChannelOutboundHandlerAdapter(){
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                byte []bytes = new byte[((ByteBuf)msg).readableBytes()];
                                ((ByteBuf)msg).getBytes(0, bytes);
                                System.out.println("port 1 server value:"+ new String(bytes));
                                ctx.writeAndFlush(ctx.alloc().buffer().writeBytes(("java1-"+Thread.currentThread().getName()).getBytes()), promise);
                            }
                        }).addLast("myInboundHandlerAdapter", new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                ctx.writeAndFlush(ctx.alloc().buffer().writeBytes("hello1".getBytes()));
                            }
                        });
                    }
                }).bind(8081);
        new ServerBootstrap().group(boss, work).channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline().addLast("myOutboundHandlerAdapter2", new ChannelOutboundHandlerAdapter(){
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                byte []bytes = new byte[((ByteBuf)msg).readableBytes()];
                                ((ByteBuf)msg).getBytes(0, bytes);
                                System.out.println("port 2 server value:"+ new String(bytes));
                                ctx.writeAndFlush(ctx.alloc().buffer().writeBytes(("java2-"+Thread.currentThread().getName()).getBytes()), promise);
                            }
                        }).addLast("myInboundHandlerAdapter", new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                ctx.writeAndFlush(ctx.alloc().buffer().writeBytes("hello2".getBytes()));
                            }
                        });
                    }
                }).bind(8080).sync();
    }
}
