package com.github.arteam.simplejsonrpc.client.generator;

/**
 * Date: 1/12/15
 * Time: 11:38 PM
 * <p>
 * Generates secure random positive integers under limit
 * By default the limit is 65536
 */
public class SecureRandomIntIdGenerator extends SecureRandomIdGenerator<Integer> {

    private static final int DEFAULT_LIMIT = 65536;

    private final int limit;

    public SecureRandomIntIdGenerator() {
        limit = DEFAULT_LIMIT;
    }

    public SecureRandomIntIdGenerator(int limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("Limit should be positive");
        }
        this.limit = limit;
    }

    @Override
    public Integer generate() {
        return secureRandom.nextInt(limit);
    }
}
