package com.github.arteam.simplejsonrpc.client.generator;

/**
 * Date: 24.08.14
 * Time: 18:20
 *
 * @author Artem Prigoda
 */
public class CurrentTimeIdGenerator implements IdGenerator<Long> {
    @Override
    public Long generate() {
        return System.currentTimeMillis();
    }
}
