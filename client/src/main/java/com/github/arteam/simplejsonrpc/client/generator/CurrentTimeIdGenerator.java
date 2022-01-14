package com.github.arteam.simplejsonrpc.client.generator;

/**
 * Date: 24.08.14
 * Time: 18:20
 * <p>
 * Return the current time as an id.
 * Not reliable if you need to guarantee uniqueness of request ids
 */
public class CurrentTimeIdGenerator implements IdGenerator<Long> {
    @Override
    public Long generate() {
        return System.currentTimeMillis();
    }
}
