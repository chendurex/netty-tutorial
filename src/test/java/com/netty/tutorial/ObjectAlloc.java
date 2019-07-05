package com.netty.tutorial;

/**
 * @author cheny.huang
 * @date 2019-07-05 09:40.
 */
public class ObjectAlloc {
    private int a;
    public int b;
    int c;
    protected int d;
    static int g;

    static class ObjectAlloc2 extends ObjectAlloc {
        private int a;
        public int b;
        int c;
        protected int d;
        static int g;
    }
}
