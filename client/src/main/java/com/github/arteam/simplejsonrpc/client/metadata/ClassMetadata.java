package com.github.arteam.simplejsonrpc.client.metadata;

import com.github.arteam.simplejsonrpc.client.ParamsType;
import com.github.arteam.simplejsonrpc.client.generator.IdGenerator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Date: 8/1/14
 * Time: 7:42 PM
 * Metadata about a Java class
 */
public class ClassMetadata {

    @Nullable
    private final ParamsType paramsType;

    @NotNull
    private final IdGenerator<?> idGenerator;

    /**
     * Map of JSON-RPC 2.0 methods by rpc name
     */
    @NotNull
    private final Map<Method, MethodMetadata> methods;

    public ClassMetadata(@Nullable ParamsType paramsType,
                         @NotNull IdGenerator<?> idGenerator,
                         @NotNull Map<Method, MethodMetadata> methods)
    {
        this.paramsType     = paramsType;
        this.idGenerator    = idGenerator;
        this.methods        = methods;
    }

    @Nullable
    public ParamsType getParamsType() {
        return paramsType;
    }

    @NotNull
    public IdGenerator<?> getIdGenerator() {
        return idGenerator;
    }

    @NotNull
    public Map<Method, MethodMetadata> getMethods() {
        return methods;
    }
}
