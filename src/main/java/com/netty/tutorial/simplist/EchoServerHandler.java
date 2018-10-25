package com.netty.tutorial.simplist;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author cheny.huang
 * @date 2018-10-24 11:31.
 */
public class EchoServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        byte []b = new byte[11];
        String s = "hello world";
        for (int i=0; i<s.length();i++) {
            b[i] = (byte)s.charAt(i);
        }
        ctx.writeAndFlush(ctx.alloc().buffer(5).writeBytes(b));
    }
}
