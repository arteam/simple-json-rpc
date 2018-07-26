package com.github.arteam.simplejsonrpc.client.exception;

import com.github.arteam.simplejsonrpc.core.domain.ErrorMessage;
import org.jetbrains.annotations.NotNull;

/**
 * Date: 8/9/14
 * Time: 10:08 PM
 * Represents JSON-RPC error returned by a server
 */
public class JsonRpcException extends RuntimeException {

    /**
     * Actual error message
     */
    @NotNull
    private ErrorMessage errorMessage;

    public JsonRpcException(@NotNull ErrorMessage errorMessage) {
        super(errorMessage.toString());
        this.errorMessage = errorMessage;
    }

    @NotNull
    public ErrorMessage getErrorMessage() {
        return errorMessage;
    }
}
