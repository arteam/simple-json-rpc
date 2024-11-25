package com.github.arteam.simplejsonrpc.server.metadata;

import java.util.Map;
import java.util.Objects;

/**
 * Date: 8/1/14
 * Time: 7:42 PM
 * <p>
 * Metadata about a Java class
 */
public final class ClassMetadata {
    private final boolean service;
    private final Map<String, MethodMetadata> methods;

    /**
     *
     */
    public ClassMetadata(boolean service,
                         Map<String, MethodMetadata> methods) {
        this.service = service;
        this.methods = methods;
    }

    public boolean service() {
        return service;
    }

    public Map<String, MethodMetadata> methods() {
        return methods;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ClassMetadata) obj;
        return this.service == that.service &&
                Objects.equals(this.methods, that.methods);
    }

    @Override
    public int hashCode() {
        return Objects.hash(service, methods);
    }

    @Override
    public String toString() {
        return "ClassMetadata[" +
                "service=" + service + ", " +
                "methods=" + methods + ']';
    }

}
