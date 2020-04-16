package com.github.arteam.simplejsonrpc.client.generator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SecureRandomIntIdGeneratorTest {

    @Test
    public void testDefaultLimit() {
        SecureRandomIntIdGenerator generator = new SecureRandomIntIdGenerator();
        Integer value = generator.generate();
        assertTrue(value >= 0);
        assertTrue(value < 65536);
    }

    @Test
    public void testGenerate() throws Exception {
        int limit = 1000000;
        SecureRandomIntIdGenerator generator = new SecureRandomIntIdGenerator(limit);
        int amount = 100;
        for (int i = 0; i < amount; i++) {
            Integer value = generator.generate();
            assertTrue(value >= 0);
            assertTrue(value < limit);
        }
    }

    @Test
    public void testPositiveLimit() {
        assertThrows(IllegalArgumentException.class,
                () -> new SecureRandomIntIdGenerator(-1));
    }
}