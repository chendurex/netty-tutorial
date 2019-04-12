package com.netty.tutorial.http;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.WriteBufferWaterMark;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 通过高低水位进行流控
 * @author cheny.huang
 * @date 2018-10-24 15:03.
 */
@Slf4j
public class WaterMarkHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        byte[] b = "hello world".getBytes();
        for (int i=0;i<200;i++) {
            // write或者writeAndFlush都是会写到缓存中去，如果对方消费过慢，那么会导致本地网络缓冲填满，最终发送速度变慢
            // 通过高低水位可以实现应用层流控
            // 因为netty都是异步的，所以通过增加监听器来判断消息真正写入网卡的时间
            // 如果使用write方法的话，数据首先会落在发送队列中，并不会写入到网卡缓冲上，一旦达到高水位，isWritable则返回false
            // 如果使用writeAndFlush，那么每次都会直接把数据写入到网卡缓冲上，所以一般不会出现高水位的情况
            // 还有一个非常重要的配置项就是SO_SENDBUF的大小设置
            // 因为高低水位判断的标准是以队列中待发送消息数目，如果使用write的话，那么每次数据都是先存储在发送队列中，那么
            // 高低水位的判断就是队列中存储的数据大小一致；如果是使用writeAndFlush的话，因为每次写完消息就会马上刷新到网卡中
            // 所以如果SO_SENDBUF非常大的话，队列也不会积压消息，那么自然就不会引起高水位的情况发生，如果SO_SENDBUF比较小的话
            // 而且对方消费速度非常慢，导致消息积压，从而就会引起高水位的发生
            ctx.writeAndFlush(ctx.alloc().buffer(24).writeBytes(b));
            ctx.writeAndFlush(ctx.alloc().buffer(24).writeZero(100)).addListener(f-> log.info("suc:{}",f.isSuccess()));
            if (!channel.isWritable()) {
                log.info("unwritable:{}, writable:{}, iswirtable:{}",
                        channel.bytesBeforeUnwritable(), channel.bytesBeforeWritable(), channel.isWritable());
            }
        }
    }
}
