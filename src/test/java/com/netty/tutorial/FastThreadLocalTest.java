package com.netty.tutorial;

import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.concurrent.FastThreadLocalThread;
import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author cheny.huang
 * @date 2019-07-05 11:15.
 */
public class FastThreadLocalTest {
    @Test
    public void testFastThreadLocal() throws InterruptedException {
        FastThreadLocal<Integer> fastThreadLocal = new FastThreadLocal<>();
        AtomicInteger atomicInteger = new AtomicInteger(1);
        for(int i=0; i<100;i++) {
            new FastThreadLocalThread(()->fastThreadLocal.set(atomicInteger.incrementAndGet())).start();
        }
        TimeUnit.SECONDS.sleep(10);
        new FastThreadLocalThread(()->fastThreadLocal.set(atomicInteger.incrementAndGet())).start();
        TimeUnit.SECONDS.sleep(100);
    }
}
