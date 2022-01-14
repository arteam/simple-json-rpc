package com.github.arteam.simplejsonrpc.client.exception;

import com.github.arteam.simplejsonrpc.core.domain.ErrorMessage;

import java.util.Map;

/**
 * Date: 10/13/14
 * Time: 8:17 PM
 * <p>
 * Exception that occurs when batch JSON-RPC request is not completely successful
 */
public class JsonRpcBatchException extends RuntimeException {

    /**
     * Succeeded requests
     */
    private Map<?, ?> successes;

    /**
     * Failed requests
     */
    private Map<?, ErrorMessage> errors;

    public JsonRpcBatchException(String message, Map<?, ?> successes, Map<?, ErrorMessage> errors) {
        super(message);
        this.successes = successes;
        this.errors = errors;
    }


    public Map<?, ?> getSuccesses() {
        return successes;
    }


    public Map<?, ErrorMessage> getErrors() {
        return errors;
    }
}
