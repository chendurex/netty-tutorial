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
public class AsyncServer {
    public static void main(String[] args) throws InterruptedException {
        Executor executor = Executors.newSingleThreadExecutor();
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup work = new NioEventLoopGroup(10);
        new ServerBootstrap().group(boss, work).channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        // 绑定的顺序直接影响到输出顺序
                        ch.pipeline().addLast("myOutboundHandlerAdapter", new ChannelOutboundHandlerAdapter(){
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                byte []bytes = new byte[((ByteBuf)msg).readableBytes()];
                                ((ByteBuf)msg).getBytes(0, bytes);
                                System.out.println("server write value:"+ new String(bytes) + ", and rewrite value to world");
                                System.out.println("current thread:"+ Thread.currentThread().getName());
                                // 异步的执行请求，会将数据写入task任务中，然后交由当前channel绑定的线程执行
                                // netty保证在整个数据传输过程中，是同一个线程执行，防止多线程引起的数据共享问题
                                executor.execute(()->
                                        ctx.writeAndFlush(ctx.alloc().buffer().writeBytes(("word-"+Thread.currentThread().getName()).getBytes()), promise));
                            }
                        }).addLast("myOutboundHandlerAdapter2", new ChannelOutboundHandlerAdapter(){
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                byte []bytes = new byte[((ByteBuf)msg).readableBytes()];
                                ((ByteBuf)msg).getBytes(0, bytes);
                                System.out.println("received server value:"+ new String(bytes) + ", and rewrite value to java");
                                System.out.println("current thread:"+ Thread.currentThread().getName());
                                executor.execute(()->
                                        ctx.writeAndFlush(ctx.alloc().buffer().writeBytes(("java-"+Thread.currentThread().getName()).getBytes()), promise));
                            }
                        }).addLast("myInboundHandlerAdapter", new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                ctx.writeAndFlush(ctx.alloc().buffer().writeBytes("hello".getBytes()));
                            }
                        });
                    }
                }).bind(8080).sync();
    }
}
