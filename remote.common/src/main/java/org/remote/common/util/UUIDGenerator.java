package org.remote.common.util;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by jingtian.zjt on 2014/12/8.
 */
public class UUIDGenerator {
    private static AtomicLong generator = new AtomicLong(0);

    public static long get() {
        return generator.getAndIncrement();
    }
}
