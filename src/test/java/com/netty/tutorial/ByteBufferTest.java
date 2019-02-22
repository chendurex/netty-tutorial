package com.netty.tutorial;

import org.junit.Assert;
import org.junit.Test;

import java.nio.ByteBuffer;

/**
 * @author cheny.huang
 * @date 2019-02-15 14:29.
 */
public class ByteBufferTest {

    @Test
    public void testDuplicate() {
        String hello = "hello";
        String world = "world";
        ByteBuffer buf = ByteBuffer.allocate(1024);
        buf.put(hello.getBytes());
        // 复制一份内存引用，它们共享同一个内存地址，但是ByteBuffer中的position等不同
        ByteBuffer duplicate = buf.duplicate();
        Assert.assertTrue(new String(buf.array()).trim().equals(hello));
        Assert.assertTrue(new String(duplicate.array()).trim().equals(hello));
        // 其中一个更新了数据，另外一个也同样更新数据
        duplicate.put(hello.getBytes());
        Assert.assertEquals(new String(buf.array()).trim(), hello+hello);
        Assert.assertEquals(new String(duplicate.array()).trim(), hello+hello);
        // 数据虽然发生变更，但是因为position并未发生变更，所以数据还是从原来的位置写入，导致覆盖了duplicate写入的数据
        buf.put(world.getBytes());
        Assert.assertEquals(new String(buf.array()).trim(), hello+world);
        Assert.assertEquals(new String(duplicate.array()).trim(), hello+world);
    }
}
