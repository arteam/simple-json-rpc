package com.github.arteam.simplejsonrpc.client.generator;

import com.google.common.collect.Sets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Set;

public class SecureRandomLongIdGeneratorTest {

    @Test
    public void testGenerate() {
        SecureRandomLongIdGenerator generator = new SecureRandomLongIdGenerator();
        int amount = 100;
        Set<Long> ids = Sets.newHashSetWithExpectedSize(amount);
        for (int i = 0; i < amount; i++) {
            Long id = generator.generate();
            Assertions.assertTrue(id > 0);
            System.out.println(id);
            ids.add(id);
        }
        Assertions.assertEquals(ids.size(), amount);
    }
}