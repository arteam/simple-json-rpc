package com.github.arteam.simplejsonrpc.server.metadata;

import com.google.common.base.MoreObjects;

import java.lang.reflect.Type;

/**
 * Date: 8/1/14
 * Time: 7:44 PM
 * Method parameter metadata
 */
public class ParameterMetadata {

    /**
     * RPC name
     */
    private final String name;

    /**
     * Actual java type
     */
    private final Class<?> type;

    /**
     * Generic java type
     */
    private final Type genericType;

    /**
     * Index in method arguments
     */
    private final int index;

    /**
     * Whether parameter is optional
     */
    private final boolean optional;

    public ParameterMetadata(String name, Class<?> type
            , Type genericType, int index, boolean optional) {
        this.name = name;
        this.type = type;
        this.genericType = genericType;
        this.index = index;
        this.optional = optional;
    }


    public String getName() {
        return name;
    }


    public Class<?> getType() {
        return type;
    }

    public boolean isOptional() {
        return optional;
    }

    public int getIndex() {
        return index;
    }


    public Type getGenericType() {
        return genericType;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("name", name)
                .add("type", type)
                .add("genericType", genericType)
                .add("index", index)
                .add("optional", optional)
                .toString();
    }
}
