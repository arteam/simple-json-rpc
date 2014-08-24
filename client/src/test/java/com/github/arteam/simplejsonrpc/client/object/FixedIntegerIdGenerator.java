package com.github.arteam.simplejsonrpc.client.object;

import com.github.arteam.simplejsonrpc.client.generator.IdGenerator;

/**
 * Date: 24.08.14
 * Time: 18:43
 *
 * @author Artem Prigoda
 */
public class FixedIntegerIdGenerator implements IdGenerator<Integer> {

    private Integer value;

    public FixedIntegerIdGenerator(Integer value) {
        this.value = value;
    }

    @Override
    public Integer generate() {
        return value;
    }
}
