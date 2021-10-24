package com.github.arteam.simplejsonrpc.server.metadata;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;

/**
 * Date: 8/1/14
 * Time: 7:42 PM
 * Metadata about a Java method
 */
public class MethodMetadata {

    /**
     * RPC method name
     */
    @NotNull
    private final String name;

    /**
     * Actual method handle
     */
    @NotNull
    private final MethodHandle methodHandle;

    /**
     * Map of method params by RPC name
     */
    @NotNull
    private final ImmutableMap<String, ParameterMetadata> params;

    public MethodMetadata(@NotNull String name, @NotNull MethodHandle methodHandle,
                          @NotNull ImmutableMap<String, ParameterMetadata> params) {
        this.name = name;
        this.methodHandle = methodHandle;
        this.params = params;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public MethodHandle getMethodHandle() {
        return methodHandle;
    }

    @NotNull
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
