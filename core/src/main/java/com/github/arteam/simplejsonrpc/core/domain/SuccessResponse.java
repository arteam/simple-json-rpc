package com.github.arteam.simplejsonrpc.core.domain;

import com.google.gson.JsonElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Date: 07.06.14
 * Time: 12:31
 * Representation of a successful JSON-RPC response
 *
 * @author Artem Prigoda
 */
public class SuccessResponse extends Response {

    @Nullable
    private final Object result;

    public SuccessResponse(@NotNull JsonElement id,
                           @Nullable Object result) {
        super(id);
        this.result = result;
    }

    @Nullable
    public Object getResult() {
        return result;
    }
}
