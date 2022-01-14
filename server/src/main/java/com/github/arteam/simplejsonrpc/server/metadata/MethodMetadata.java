package com.github.arteam.simplejsonrpc.server.metadata;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;

import java.lang.invoke.MethodHandle;

/**
 * Date: 8/1/14
 * Time: 7:42 PM
 * <p>
 * Metadata about a Java method
 */
public class MethodMetadata {

    /**
     * RPC method name
     */
    private final String name;

    /**
     * Actual method handle
     */
    private final MethodHandle methodHandle;

    /**
     * Map of method params by RPC name
     */
    private final ImmutableMap<String, ParameterMetadata> params;

    public MethodMetadata(String name, MethodHandle methodHandle,
                          ImmutableMap<String, ParameterMetadata> params) {
        this.name = name;
        this.methodHandle = methodHandle;
        this.params = params;
    }

    public String getName() {
        return name;
    }

    public MethodHandle getMethodHandle() {
        return methodHandle;
    }

    public ImmutableMap<String, ParameterMetadata> getParams() {
        return params;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("name", name)
                .add("method", methodHandle)
                .add("params", params)
                .toString();
    }
}
