package com.github.arteam.dropwizard.json.rpc.protocol.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.NullNode;
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
    private final ErrorMessage error;

    public ErrorResponse(@NotNull ValueNode id,
                         @NotNull ErrorMessage error) {
        super(id);
        this.error = error;
    }

    public ErrorResponse(@NotNull ErrorMessage error) {
        super(NullNode.getInstance());
        this.error = error;
    }

    @NotNull
    public ErrorMessage getError() {
        return error;
    }
}
