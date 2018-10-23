package com.netty.tutorial.simplist;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author cheny.huang
 * @date 2018-10-23 15:48.
 */
@Slf4j
public class PojoDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 4) {
            return ;
        }
        UnixTime t = new UnixTime(in.readUnsignedInt());
        log.info("PojoDecoder and valus is : {},", t);
        out.add(t);
    }
}
