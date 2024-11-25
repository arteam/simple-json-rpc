package com.github.arteam.simplejsonrpc.client.metadata;

import java.util.Objects;

/**
 * Date: 8/1/14
 * Time: 7:44 PM
 * <p>
 * Method parameter metadata
 */
public final class ParameterMetadata {
    private final int index;
    private final boolean optional;

    /**
     *
     */
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
        ParameterMetadata that = (ParameterMetadata) obj;
        return this.index == that.index &&
                this.optional == that.optional;
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, optional);
    }

    @Override
    public String toString() {
        return "ParameterMetadata[" +
                "index=" + index + ", " +
                "optional=" + optional + ']';
    }

}
