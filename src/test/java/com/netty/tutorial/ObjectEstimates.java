package com.netty.tutorial;

import io.netty.util.internal.InternalThreadLocalMap;
import org.junit.Test;
import org.openjdk.jol.datamodel.X86_32_DataModel;
import org.openjdk.jol.datamodel.X86_64_COOPS_DataModel;
import org.openjdk.jol.datamodel.X86_64_DataModel;
import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.layouters.HotSpotLayouter;

import java.util.HashMap;

import static java.lang.System.out;
/**
 * @author cheny.huang
 * @date 2019-07-05 09:04.
 */
public class ObjectEstimates extends ClasspathedOperation {

    @Override
    public String label() {
        return "estimates";
    }

    @Override
    public String description() {
        return "Simulate the class layout in different VM modes.";
    }

    @Override
    protected void runWith(Class<?> klass) throws Exception {
        out.println("***** 32-bit VM: **********************************************************");
        out.println(ClassLayout.parseClass(klass, new HotSpotLayouter(new X86_32_DataModel())).toPrintable());

        out.println("***** 64-bit VM: **********************************************************");
        out.println(ClassLayout.parseClass(klass, new HotSpotLayouter(new X86_64_DataModel())).toPrintable());

        out.println("***** 64-bit VM, compressed references enabled: ***************************");
        out.println(ClassLayout.parseClass(klass, new HotSpotLayouter(new X86_64_COOPS_DataModel())).toPrintable());

        out.println("***** 64-bit VM, compressed references enabled, 16-byte align: ************");
        out.println(ClassLayout.parseClass(klass, new HotSpotLayouter(new X86_64_COOPS_DataModel(16))).toPrintable());
    }

    @Test
    public void testHashMapObjects() throws Exception {
        runWith(InternalThreadLocalMap.class);
    }
}