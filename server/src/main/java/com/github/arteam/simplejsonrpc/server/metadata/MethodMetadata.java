package com.github.arteam.simplejsonrpc.server.metadata;

import java.lang.invoke.MethodHandle;
import java.util.Map;
import java.util.Objects;

/**
 * Date: 8/1/14
 * Time: 7:42 PM
 * <p>
 * Metadata about a Java method
 */
public final class MethodMetadata {
    private final String name;
    private final MethodHandle methodHandle;
    private final Map<String, ParameterMetadata> params;

    /**
     *
     */
    public MethodMetadata(String name, MethodHandle methodHandle,
                          Map<String, ParameterMetadata> params) {
        this.name = name;
        this.methodHandle = methodHandle;
        this.params = params;
    }

    public String name() {
        return name;
    }

    public MethodHandle methodHandle() {
        return methodHandle;
    }

    public Map<String, ParameterMetadata> params() {
        return params;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (MethodMetadata) obj;
        return Objects.equals(this.name, that.name) &&
                Objects.equals(this.methodHandle, that.methodHandle) &&
                Objects.equals(this.params, that.params);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, methodHandle, params);
    }

    @Override
    public String toString() {
        return "MethodMetadata[" +
                "name=" + name + ", " +
                "methodHandle=" + methodHandle + ", " +
                "params=" + params + ']';
    }

}
