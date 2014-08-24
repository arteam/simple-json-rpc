package com.github.arteam.simplejsonrpc.client.object;

import com.github.arteam.simplejsonrpc.client.generator.IdGenerator;

/**
 * Date: 24.08.14
 * Time: 18:41
 *
 * @author Artem Prigoda
 */
public class FixedStringIdGenerator implements IdGenerator<String> {

    private String value;

    public FixedStringIdGenerator(String value) {
        this.value = value;
    }

    @Override
    public String generate() {
        return value;
    }
}
