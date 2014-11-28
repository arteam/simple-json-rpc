package com.github.arteam.simplejsonrpc.client.metadata;

import java.lang.reflect.Type;

/**
 * Date: 8/1/14
 * Time: 7:44 PM
 * Method parameter metadata
 *
 * @author Artem Prigoda
 */
public class ParameterMetadata {

    /**
     * Index in method arguments
     */
    private final int index;

    /**
     * Whether parameter is optional
     */
    private final boolean optional;

    /**
     * Parameter type
     */
    private final Type type;

    public ParameterMetadata(int index, boolean optional, Type type) {
        this.index = index;
        this.optional = optional;
        this.type = type;
    }

    public int getIndex() {
        return index;
    }

    public boolean isOptional() {
        return optional;
    }

    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return "ParameterMetadata{" +
                "index=" + index +
                ", optional=" + optional +
                ", type=" + type +  "}";
    }
}
