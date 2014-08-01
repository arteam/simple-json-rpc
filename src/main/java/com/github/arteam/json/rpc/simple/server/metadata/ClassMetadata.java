package com.github.arteam.json.rpc.simple.server.metadata;

import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.NotNull;

/**
 * Date: 8/1/14
 * Time: 7:42 PM
 * Metadata about a Java class
 *
 * @author Artem Prigoda
 */
public class ClassMetadata {

    /**
     * Whether class JSON-RPC 2.0 service
     */
    private final boolean service;

    /**
     * Map of JSON-RPC 2.0 methods by rpc name
     */
    @NotNull
    private final ImmutableMap<String, MethodMetadata> methods;

    public ClassMetadata(boolean service, @NotNull ImmutableMap<String, MethodMetadata> methods) {
        this.service = service;
        this.methods = methods;
    }

    public boolean isService() {
        return service;
    }

    @NotNull
    public ImmutableMap<String, MethodMetadata> getMethods() {
        return methods;
    }
}
