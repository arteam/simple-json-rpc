package com.github.arteam.simplejsonrpc.client.generator;

import org.jetbrains.annotations.NotNull;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Date: 1/12/15
 * Time: 11:17 PM
 * Abstract generator of secure random identifiers
 */
abstract class SecureRandomIdGenerator<T> implements IdGenerator<T> {

    private static final String SHA_1_PRNG = "SHA1PRNG";

    @NotNull
    protected final SecureRandom secureRandom;

    protected SecureRandomIdGenerator() {
        secureRandom = initSecureRandom();
    }

    @NotNull
    private static SecureRandom initSecureRandom() {
        try {
            SecureRandom secureRandom = SecureRandom.getInstance(SHA_1_PRNG);
            secureRandom.nextBytes(new byte[]{}); // Important to seed immediately after creation
            return secureRandom;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
