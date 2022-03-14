package com.github.arteam.simplejsonrpc.client.metadata;

/**
 * Date: 8/1/14
 * Time: 7:44 PM
 * <p>
 * Method parameter metadata
 */
public record ParameterMetadata(int index, boolean optional) {

    public int getIndex() {
        return index;
    }

    public boolean isOptional() {
        return optional;
    }
}
