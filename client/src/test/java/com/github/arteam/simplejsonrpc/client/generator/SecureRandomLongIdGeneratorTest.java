package com.github.arteam.simplejsonrpc.client.generator;

import com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.*;

public class SecureRandomLongIdGeneratorTest {

    @Test
    public void testGenerate()  {
        SecureRandomLongIdGenerator generator = new SecureRandomLongIdGenerator();
        int amount = 100;
        Set<Long> ids = Sets.newHashSetWithExpectedSize(amount);
        for (int i = 0; i < amount; i++) {
            Long id = generator.generate();
            Assert.assertTrue(id.longValue() > 0);
            System.out.println(id);
            ids.add(id);
        }
        Assert.assertEquals(ids.size(), amount);
    }
}