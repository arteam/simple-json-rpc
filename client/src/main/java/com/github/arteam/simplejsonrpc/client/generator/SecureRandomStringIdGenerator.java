package com.github.arteam.simplejsonrpc.client.generator;

import org.jetbrains.annotations.NotNull;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Date: 12/30/14
 * Time: 11:45 PM
 * Generate secure random strings consisting of HEX symbols with length of 40 characters
 *
 * @author Artem Prigoda
 */
public class SecureRandomStringIdGenerator implements IdGenerator<String> {

    private static final char[] ALPHABET = "0123456789abcdef".toCharArray();
    private static final String SHA_1_PRNG = "SHA1PRNG";
    private static final int DEFAULT_CHUNK_SIZE = 20;

    private final SecureRandom secureRandom;
    private final int chunkSize;

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

    /**
     * Create a default generator
     */
    public SecureRandomStringIdGenerator() {
        secureRandom = initSecureRandom();
        chunkSize = DEFAULT_CHUNK_SIZE;
    }

    /**
     * Create generator with a specific identifiers length
     *
     * @param idLength custom identifier length (it should pow of 2)
     */
    public SecureRandomStringIdGenerator(int idLength) {
        if (idLength < 2) {
            throw new IllegalArgumentException("Bad message length: '" + idLength + "'. It should be >= 2");
        }
        secureRandom = initSecureRandom();
        this.chunkSize = idLength / 2;
    }

    @Override
    public String generate() {
        byte[] buffer = new byte[chunkSize];
        secureRandom.nextBytes(buffer);
        return hexString(buffer);
    }


    /**
     * Convert binary data to HEX representation.
     * Every byte is converted to 2 HEX symbols (one symbol for every 4 bits)
     *
     * @param source source chunk of data
     * @return string representation of the chunk as HEX values
     */
    @NotNull
    private static String hexString(@NotNull byte[] source) {
        char[] result = new char[source.length * 2];
        for (int i = 0; i < source.length; i++) {
            int unsigned = source[i] & 0xFF;
            int first4Bytes = unsigned >>> 4;
            int last4Bytes = unsigned & 0x0F;
            result[i * 2] = ALPHABET[first4Bytes];
            result[i * 2 + 1] = ALPHABET[last4Bytes];
        }
        return new String(result);
    }
}
