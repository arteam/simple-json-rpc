package com.github.arteam.simplejsonrpc.client.metadata;

import com.github.arteam.simplejsonrpc.client.ParamsType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Date: 8/1/14
 * Time: 7:42 PM
 * Metadata about a Java method
 *
 * @author Artem Prigoda
 */
public class MethodMetadata {

    @NotNull
    private final String name;

    @Nullable
    private final ParamsType paramsType;

    /**
     * Map of method params by RPC name
     */
    @NotNull
    private final Map<String, ParameterMetadata> params;

    public MethodMetadata(@NotNull String name, @Nullable ParamsType paramsType, @NotNull Map<String, ParameterMetadata> params) {
        this.params = params;
        this.name = name;
        this.paramsType = paramsType;
    }
    @NotNull
    public Map<String, ParameterMetadata> getParams() {
        return params;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @Nullable
    public ParamsType getParamsType() {
        return paramsType;
    }

    @Override
    public String toString() {
        return "MethodMetadata{" +
                " params=" + params +
                '}';
    }
}
