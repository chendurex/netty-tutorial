package com.netty.tutorial.simplist;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author cheny.huang
 * @date 2018-10-23 16:27.
 */
public class UnixTimeEncoder extends MessageToByteEncoder<UnixTime> {
    @Override
    protected void encode(ChannelHandlerContext ctx, UnixTime msg, ByteBuf out) throws Exception {
        out.writeInt((int)msg.value());
    }
}
