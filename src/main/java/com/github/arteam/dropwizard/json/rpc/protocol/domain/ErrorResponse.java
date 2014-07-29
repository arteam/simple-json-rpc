package com.github.arteam.dropwizard.json.rpc.protocol.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ValueNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Date: 07.06.14
 * Time: 12:35
 * JSON-RPC error response
 *
 * @author Artem Prigoda
 */
public class ErrorResponse extends Response {

    @NotNull
    @JsonProperty("error")
    private final Object error;

    public ErrorResponse(@Nullable ValueNode id,
                        @NotNull ErrorMessage error) {
        super(id);
        this.error = error;
    }

    public ErrorResponse(@NotNull ErrorMessage error) {
        super(null);
        this.error = error;
    }

    @NotNull
    public Object getError() {
        return error;
    }
}
