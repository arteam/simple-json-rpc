package com.github.arteam.simplejsonrpc.client.metadata;

/**
 * Date: 8/1/14
 * Time: 7:44 PM
 * Method parameter metadata
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

    public ParameterMetadata(int index, boolean optional) {
        this.index = index;
        this.optional = optional;
    }

    public int getIndex() {
        return index;
    }

    public boolean isOptional() {
        return optional;
    }

    @Override
    public String toString() {
        return "ParameterMetadata{" +
                "index=" + index +
                ", optional=" + optional +
                '}';
    }
}
