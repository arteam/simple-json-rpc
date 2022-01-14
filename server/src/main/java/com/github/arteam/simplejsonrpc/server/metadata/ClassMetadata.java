package com.github.arteam.simplejsonrpc.server.metadata;

import com.google.common.collect.ImmutableMap;

/**
 * Date: 8/1/14
 * Time: 7:42 PM
 * Metadata about a Java class
 */
public class ClassMetadata {

    /**
     * Whether class JSON-RPC 2.0 service
     */
    private final boolean service;

    /**
     * Map of JSON-RPC 2.0 methods by rpc name
     */
    private final ImmutableMap<String, MethodMetadata> methods;

    public ClassMetadata(boolean service, ImmutableMap<String, MethodMetadata> methods) {
        this.service = service;
        this.methods = methods;
    }

    public boolean isService() {
        return service;
    }

    public ImmutableMap<String, MethodMetadata> getMethods() {
        return methods;
    }
}
