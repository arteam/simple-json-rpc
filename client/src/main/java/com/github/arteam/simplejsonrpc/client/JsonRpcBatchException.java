package com.github.arteam.simplejsonrpc.client;

import com.github.arteam.simplejsonrpc.core.domain.ErrorMessage;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Date: 10/13/14
 * Time: 8:17 PM
 *
 * @author Artem Prigoda
 */
public class JsonRpcBatchException extends RuntimeException {

    @NotNull
    private Map<?, ?> successes;

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
