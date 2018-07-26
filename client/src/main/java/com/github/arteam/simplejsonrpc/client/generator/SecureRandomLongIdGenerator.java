package com.github.arteam.simplejsonrpc.client.generator;

/**
 * Date: 1/12/15
 * Time: 11:12 PM
 * Generate secure random positive long identifiers
 */
public class SecureRandomLongIdGenerator extends SecureRandomIdGenerator<Long> {

    @Override
    public Long generate() {
        return secureRandom.nextLong() >>> 1;
    }
}
