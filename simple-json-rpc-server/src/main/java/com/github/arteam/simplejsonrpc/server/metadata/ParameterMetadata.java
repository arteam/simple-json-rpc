package com.github.arteam.simplejsonrpc.server.metadata;

import com.google.common.base.Objects;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

/**
 * Date: 8/1/14
 * Time: 7:44 PM
 * Method parameter metadata
 *
 * @author Artem Prigoda
 */
public class ParameterMetadata {

    /**
     * RPC name
     */
    @NotNull
    private final String name;

    /**
     * Actual java type
     */
    @NotNull
    private final Class<?> type;

    /**
     * Generic java type
     */
    @NotNull
    private final Type genericType;

    /**
     * Index in method arguments
     */
    private final int index;

    /**
     * Whether parameter is optional
     */
    private final boolean optional;

    public ParameterMetadata(@NotNull String name, @NotNull Class<?> type
            , @NotNull Type genericType, int index, boolean optional) {
        this.name = name;
        this.type = type;
        this.genericType = genericType;
        this.index = index;
        this.optional = optional;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public Class<?> getType() {
        return type;
    }

    public boolean isOptional() {
        return optional;
    }

    public int getIndex() {
        return index;
    }

    @NotNull
    public Type getGenericType() {
        return genericType;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("name", name)
                .add("type", type)
                .add("genericType", genericType)
                .add("index", index)
                .add("optional", optional)
                .toString();
    }
}
