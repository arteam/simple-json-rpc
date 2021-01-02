package com.github.arteam.simplejsonrpc.client.metadata;

import org.jetbrains.annotations.NotNull;

public interface ServiceMetadataFactory {

    /**
     * Gets remote service interface metadata
     *
     * @param serviceClass an interface for representing a remote service
     * @return class metadata
     */
    @NotNull
    ServiceMetadata createServiceMetadata(@NotNull Class<?> serviceClass);
}
