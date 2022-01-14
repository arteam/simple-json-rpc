package com.github.arteam.simplejsonrpc.client.generator;

import java.security.SecureRandom;

/**
 * Date: 1/12/15
 * Time: 11:17 PM
 * <p>
 * Abstract generator of secure random identifiers
 */
abstract class SecureRandomIdGenerator<T> implements IdGenerator<T> {

    protected final SecureRandom secureRandom;

    protected SecureRandomIdGenerator() {
        this(new SecureRandom());
    }

    protected SecureRandomIdGenerator(SecureRandom secureRandom) {
        this.secureRandom = secureRandom;
    }
}
