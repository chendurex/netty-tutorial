package com.netty.tutorial.simplist;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

/**
 * @author cheny.huang
 * @date 2019-05-09 10:34.
 */
@Slf4j
public class LineBaseServer {
    public static void main(String[] args) {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        try {
            new ServerBootstrap().group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer(){
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            /**
                             * 如果按照一定的规则切割数据流的话，只要满足条件则触发一次回调,比如下面按行切割数据，如果有N行
                             * 数据，那么会触发N次channelRead0，一旦数据被触发过后，数据就会被丢弃，就算是还存在未消费的数据
                             * 也会被丢弃(比如ByteBuffer有20个字符，而实际消费了10字符，只要此次流程处理完毕，那么数据也会被清理)
                             * {@link AbstractNioByteChannel#read}，我有个疑问？假如两次数据同时传递过来，但是后一次数据
                             * 中某些内容丢失网络包延迟一定时间才传递过来，导致第一次请求成功，第二次请求的数据则因为丢弃导致失败
                             * 这个场景会出现吗？看了Netty获取数据流的场景，其依赖的是JDK获取数据流，所以问题交给jdk是如何
                             * 处理的，最后发现是返回-1则表示没有数据了，这个没有数据是怎么确认的？tcp数据流是随时都有可能
                             * 发送过来，怎么确定数据传送完毕了？
                             */
                            ch.pipeline().addLast(new LineBasedFrameDecoder(99));
                            ch.pipeline().addLast(new SimpleChannelInboundHandler<ByteBuf> (){
                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
                                    byte[] b = new byte[msg.readableBytes()];
                                    msg.getBytes(0, b);
                                    log.info("string values:{}", new String(b));
                                }

                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    log.info("channel active");
                                    super.channelActive(ctx);
                                }
                            });
                        }
                    })
                    .bind(8080).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }

    }
}
