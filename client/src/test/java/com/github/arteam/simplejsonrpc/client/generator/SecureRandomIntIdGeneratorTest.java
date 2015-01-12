package com.github.arteam.simplejsonrpc.client.generator;

import com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.*;

public class SecureRandomIntIdGeneratorTest {

    @Test
    public void testDefaultLimit() {
        SecureRandomIntIdGenerator generator = new SecureRandomIntIdGenerator();
        Integer value = generator.generate();
        Assert.assertTrue(value >= 0);
        Assert.assertTrue(value < 65536);
    }

    @Test
    public void testGenerate() throws Exception {
        int limit = 1000000;
        SecureRandomIntIdGenerator generator = new SecureRandomIntIdGenerator(limit);
        int amount = 100;
        Set<Integer> ids = Sets.newHashSet();
        for (int i = 0; i < amount; i++) {
            Integer value = generator.generate();
            System.out.println(value);
            Assert.assertTrue(value >= 0);
            Assert.assertTrue(value < limit);
            ids.add(value);
        }

        Assert.assertEquals(ids.size(), amount);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPositiveLimit() {
        new SecureRandomIntIdGenerator(-1);
    }
}