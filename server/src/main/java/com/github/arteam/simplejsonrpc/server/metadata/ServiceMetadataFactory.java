package com.github.arteam.simplejsonrpc.server.metadata;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface ServiceMetadataFactory {

    /**
     * Gets class metadata for JSON-RPC processing.
     * It scans the class and builds JSON-RPC meta-information about methods and it's parameters
     *
     * @param serviceClass actual service class
     * @return service class JSON-RPC meta-information
     */
    @NotNull
    ServiceMetadata createServiceMetadata(@NotNull Class<?> serviceClass);

    /**
     *
     * @param throwableClass
     * @return
     */
    @NotNull
    ErrorDataResolver buildErrorDataResolver(@NotNull Class<? extends Throwable> throwableClass);

    /**
     *
     * @param rootCause
     * @return
     */
    @NotNull
    Optional<ErrorMetadata> createErrorMetadata(@NotNull Throwable rootCause );
}
