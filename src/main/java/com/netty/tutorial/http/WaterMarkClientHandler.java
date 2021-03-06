package com.netty.tutorial.http;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author cheny.huang
 * @date 2018-10-24 11:06.
 */
@Slf4j
public class WaterMarkClientHandler extends ChannelInboundHandlerAdapter {
    private int count;
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf b = (ByteBuf)msg;
        byte[] buf = new byte[b.readableBytes()];
        b.readBytes(buf);
        count += buf.length;
        log.info("count:{} water client length is :{} and value:{}", count, buf.length, new String(buf));
        TimeUnit.SECONDS.sleep(1);
    }

    /**
     * 仅仅代表当前缓冲区的数据读完了，并不代表整个请求的数据发送完毕了
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        log.info("water client read complete");
    }
}
