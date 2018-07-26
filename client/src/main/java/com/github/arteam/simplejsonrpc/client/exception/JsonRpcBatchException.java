package com.github.arteam.simplejsonrpc.client.exception;

import com.github.arteam.simplejsonrpc.core.domain.ErrorMessage;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Date: 10/13/14
 * Time: 8:17 PM
 * Exception that occurs when batch JSON-RPC request is not completely successful
 */
public class JsonRpcBatchException extends RuntimeException {

    /**
     * Succeeded requests
     */
    @NotNull
    private Map<?, ?> successes;

    /**
     * Failed requests
     */
    @NotNull
    private Map<?, ErrorMessage> errors;

    public JsonRpcBatchException(String message, @NotNull Map<?, ?> successes, @NotNull Map<?, ErrorMessage> errors) {
        super(message);
        this.successes = successes;
        this.errors = errors;
    }

    @NotNull
    public Map<?, ?> getSuccesses() {
        return successes;
    }

    @NotNull
    public Map<?, ErrorMessage> getErrors() {
        return errors;
    }
}
