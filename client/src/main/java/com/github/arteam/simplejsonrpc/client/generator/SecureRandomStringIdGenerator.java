package com.github.arteam.simplejsonrpc.client.generator;

import org.jetbrains.annotations.NotNull;

/**
 * Date: 12/30/14
 * Time: 11:45 PM
 * Generate secure random strings consisting of HEX symbols with length of 40 characters
 */
public class SecureRandomStringIdGenerator extends SecureRandomIdGenerator<String> {

    private static final char[] ALPHABET = "0123456789abcdef".toCharArray();
    private static final int DEFAULT_CHUNK_SIZE = 20;

    private final int chunkSize;

    /**
     * Create a default generator
     */
    public SecureRandomStringIdGenerator() {
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
            int first4Bits = unsigned >>> 4;
            int last4Bits = unsigned & 0x0F;
            result[i * 2] = ALPHABET[first4Bits];
            result[i * 2 + 1] = ALPHABET[last4Bits];
        }
        return new String(result);
    }
}
