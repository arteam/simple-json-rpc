package com.github.arteam.simplejsonrpc.client.generator;

import com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Set;

public class SecureRandomStringIdGeneratorTest {

    private static final char[] ALPHABET = "0123456789abcdef".toCharArray();
    private SecureRandomStringIdGenerator secureRandomStringIdGenerator;

    @Test
    public void test() {
        secureRandomStringIdGenerator = new SecureRandomStringIdGenerator();
        testSize(40);
    }

    @Test
    public void testSpecificSize() {
        secureRandomStringIdGenerator = new SecureRandomStringIdGenerator(64);
        testSize(64);
    }

    private void testSize(int size) {
        int amount = 100;
        Set<String> ids = Sets.newHashSetWithExpectedSize(amount);
        for (int i = 0; i < amount; i++) {
            String id = secureRandomStringIdGenerator.generate();
            System.out.println(id);
            Assert.assertEquals(id.length(), size);
            for (char c : id.toCharArray()) {
                if (Arrays.binarySearch(ALPHABET, c) == -1) {
                    Assert.fail("Bad symbol: " + c);
                }
            }
            ids.add(id);
        }
        Assert.assertEquals(ids.size(), amount);
    }

}