package com.github.arteam.simplejsonrpc.core.domain;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import org.jetbrains.annotations.NotNull;

/**
 * Date: 07.06.14
 * Time: 12:35
 * <p/>
 * Representation of a JSON-RPC error response
 *
 * @author Artem Prigoda
 */
public class ErrorResponse extends Response {

    @NotNull
    private final ErrorMessage error;

    public ErrorResponse(@NotNull JsonElement id,
                         @NotNull ErrorMessage error) {
        super(id);
        this.error = error;
    }

    public ErrorResponse(@NotNull ErrorMessage error) {
        super(JsonNull.INSTANCE);
        this.error = error;
    }

    @NotNull
    public ErrorMessage getError() {
        return error;
    }
}
