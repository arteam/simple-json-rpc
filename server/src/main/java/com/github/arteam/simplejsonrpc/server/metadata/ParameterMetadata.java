package com.github.arteam.simplejsonrpc.server.metadata;

import java.lang.reflect.Type;
import java.util.Objects;

/**
 * Date: 8/1/14
 * Time: 7:44 PM
 * <p>
 * Method parameter metadata
 */
public class ParameterMetadata {
    private String name;
    private Class<?> type;
    private Type genericType;
    private int index;
    private boolean optional;

    /**
     *
     */
    public ParameterMetadata(String name, Class<?> type, Type genericType,
                             int index, boolean optional) {
        this.name = name;
        this.type = type;
        this.genericType = genericType;
        this.index = index;
        this.optional = optional;
    }

    public String name() {
        return name;
    }

    public Class<?> type() {
        return type;
    }

    public Type genericType() {
        return genericType;
    }

    public int index() {
        return index;
    }

    public boolean optional() {
        return optional;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ParameterMetadata) obj;
        return Objects.equals(this.name, that.name) &&
                Objects.equals(this.type, that.type) &&
                Objects.equals(this.genericType, that.genericType) &&
                this.index == that.index &&
                this.optional == that.optional;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, genericType, index, optional);
    }

    @Override
    public String toString() {
        return "ParameterMetadata[" +
                "name=" + name + ", " +
                "type=" + type + ", " +
                "genericType=" + genericType + ", " +
                "index=" + index + ", " +
                "optional=" + optional + ']';
    }

}
