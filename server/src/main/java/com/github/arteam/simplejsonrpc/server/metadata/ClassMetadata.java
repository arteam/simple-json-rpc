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

    @NotNull
    private final String serviceName;

    /**
     * factory method for create a service's Metadata
     *
     * @param serviceName
     * @param methods
     * @return
     */
    public static ClassMetadata asService( @NotNull String serviceName,  @NotNull Map<String, MethodMetadata> methods ) {
        return new ClassMetadata( true, serviceName, methods );
    }

    /**
     * factory method for create a simple class Metadata (no service)
     *
     * @param clazz
     * @return
     */
    public static ClassMetadata asClass( @NotNull Class<?> clazz ) {
        return new ClassMetadata( false, clazz.getCanonicalName(), emptyMap() );
    }

    private  ClassMetadata(boolean service,  @NotNull String serviceName,  @NotNull Map<String, MethodMetadata> methods) {
        this.serviceName = serviceName;
        this.service = service;
        this.methods = methods;
    }

    public String getServiceName() {
        return serviceName;
    }

    public boolean isService() {
        return service;
    }

    @NotNull
    public Map<String, MethodMetadata> getMethods() {
        return methods;
    }
}
