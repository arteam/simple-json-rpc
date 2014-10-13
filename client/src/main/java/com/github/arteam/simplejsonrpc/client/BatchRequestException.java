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
public class BatchRequestException extends RuntimeException {

    @NotNull
    private Map<Object, Object> successes;

    @NotNull
    private Map<Object, ErrorMessage> errors;

    public BatchRequestException(String message, @NotNull Map<Object, Object> successes, @NotNull Map<Object, ErrorMessage> errors) {
        super(message);
        this.successes = successes;
        this.errors = errors;
    }

    @NotNull
    public Map<Object, Object> getSuccesses() {
        return successes;
    }

    @NotNull
    public Map<Object, ErrorMessage> getErrors() {
        return errors;
    }
}
