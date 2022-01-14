package com.github.arteam.simplejsonrpc.client.generator;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Date: 12/30/14
 * Time: 11:19 PM
 * <p>
 * Return id from an atomic long counter.
 * It's the most reliable and straightforward way to generate identifiers
 */
public class AtomicLongIdGenerator implements IdGenerator<Long> {

    private final AtomicLong counter = new AtomicLong(0L);

    @Override
    public Long generate() {
        return counter.incrementAndGet();
    }
}
