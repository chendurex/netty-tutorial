package com.netty.tutorial.simplist;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author cheny.huang
 * @date 2018-10-24 11:06.
 */
@Slf4j
public class EchoClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf b = (ByteBuf)msg;
        byte[] buf = new byte[11];
        b.readBytes(buf);
        log.info("echo client value is :{}", new String(buf));
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        log.info("echo client read complete and closed");
        ctx.close();
    }
}
