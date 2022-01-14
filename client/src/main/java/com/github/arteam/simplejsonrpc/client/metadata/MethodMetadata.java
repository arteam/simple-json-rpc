package com.github.arteam.simplejsonrpc.client.metadata;

import com.github.arteam.simplejsonrpc.client.ParamsType;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Date: 8/1/14
 * Time: 7:42 PM
 * <p>
 * Metadata about a Java method
 */
public class MethodMetadata {

    private final String name;

    @Nullable
    private final ParamsType paramsType;

    /**
     * Map of method params by RPC name
     */
    private final Map<String, ParameterMetadata> params;

    public MethodMetadata(String name, @Nullable ParamsType paramsType, Map<String, ParameterMetadata> params) {
        this.params = params;
        this.name = name;
        this.paramsType = paramsType;
    }

    public Map<String, ParameterMetadata> getParams() {
        return params;
    }

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
