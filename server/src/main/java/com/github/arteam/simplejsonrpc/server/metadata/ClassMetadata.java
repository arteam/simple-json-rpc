package com.github.arteam.simplejsonrpc.server.metadata;

import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Map;

import static java.util.Collections.emptyMap;

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
    @NotNull
    private final Map<String, MethodMetadata> methods;

    /**
     * factory method for create a service's Metadata
     *
     * @param methods
     * @return
     */
    public static ClassMetadata asService( @NotNull Map<String, MethodMetadata> methods ) {
        return new ClassMetadata( true, methods );
    }

    /**
     * factory method for create a simple class Metadata (no service)
     *
     * @return
     */
    public static ClassMetadata asClass() {
        return new ClassMetadata( false, emptyMap() );
    }

    private  ClassMetadata(boolean service,  @NotNull Map<String, MethodMetadata> methods) {
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
