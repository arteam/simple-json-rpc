package com.github.arteam.simplejsonrpc.server.metadata;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

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
     * Actual method
     */
    @NotNull
    private final Method method;

    /**
     * Map of method params by RPC name
     */
    @NotNull
    private final java.util.Map<String, ParameterMetadata> params;

    public MethodMetadata(@NotNull String name, @NotNull Method method,
                          @NotNull java.util.Map<String, ParameterMetadata> params) {
        this.name = name;
        this.method = method;
        this.params = params;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public Method getMethod() {
        return method;
    }

    @NotNull
    public java.util.Map<String, ParameterMetadata> getParams() {
        return params;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("name", name)
                .add("method", method)
                .add("params", params)
                .toString();
    }
}
