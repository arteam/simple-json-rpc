package com.github.arteam.simplejsonrpc.client.object;

import com.github.arteam.simplejsonrpc.client.generator.IdGenerator;

/**
 * Date: 24.08.14
 * Time: 18:24
 *
 * @author Artem Prigoda
 */
public class TestIdGenerator implements IdGenerator<String> {

    private static String id;

    @Override
    public String generate() {
        return "asd671";
    }
}
