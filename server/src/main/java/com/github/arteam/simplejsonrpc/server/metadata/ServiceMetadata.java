package com.github.arteam.simplejsonrpc.server.metadata;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static java.util.Collections.emptyMap;

/**
 * Date: 8/1/14
 * Time: 7:42 PM
 * Metadata about a Java class
 */
public class ServiceMetadata {

    /**
     * Whether class JSON-RPC 2.0 service
     */
    private final boolean service;

    /**
     * Map of JSON-RPC 2.0 methods by rpc name
     */
    @NotNull
    private final Map<String, MethodMetadata> methods;

    /**
     * factory method for create a service's Metadata
     *
     * @param methods
     * @return
     */
    public static ServiceMetadata asService(@NotNull Map<String, MethodMetadata> methods ) {
        return new ServiceMetadata( true, methods );
    }

    /**
     * factory method for create a simple class Metadata (no service)
     *
     * @return
     */
    public static ServiceMetadata asClass() {
        return new ServiceMetadata( false, emptyMap() );
    }

    private ServiceMetadata(boolean service, @NotNull Map<String, MethodMetadata> methods) {
        this.service = service;
        this.methods = methods;
    }

    public boolean isService() {
        return service;
    }

    @NotNull
    public Map<String, MethodMetadata> getMethods() {
        return methods;
    }
}
