package com.github.arteam.json.rpc.simple.server.metadata;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.List;

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

    @NotNull
    private final ImmutableList<Class<?>> genericTypes;

    /**
     * Index in method arguments
     */
    private final int index;

    /**
     * Whether parameter is optional
     */
    private final boolean optional;

    public ParameterMetadata(@NotNull String name, @NotNull Class<?> type
            , @NotNull ImmutableList<Class<?>> genericTypes, int index, boolean optional) {
        this.name = name;
        this.type = type;
        this.genericTypes = genericTypes;
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
    public ImmutableList<Class<?>> getGenericTypes() {
        return genericTypes;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("name", name)
                .add("type", type)
                .add("genericType", genericTypes)
                .add("index", index)
                .add("optional", optional)
                .toString();
    }
}
