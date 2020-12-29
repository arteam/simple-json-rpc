package com.github.arteam.simplejsonrpc.client;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Optional;

import static java.util.Optional.empty;

/**
 * Date: 8/9/14
 * Time: 8:52 PM
 * <p>Abstract transport for JSON-RPC communication</p>
 */
public interface Transport {

    /**
     * Passes a JSON-RPC request in a text form to a backend and
     * returns a JSON-RPC response in a text form as well
     *
     * @Param service optional service class on which the method is invoked
     * @param request JSON-RPC request as a string
     * @return JSON-RPC response as a string
     * @throws IOException if an I/O error happens during transfer
     */
    @NotNull String pass( @NotNull Optional<Class<?>> service, @NotNull String request) throws IOException;
}
