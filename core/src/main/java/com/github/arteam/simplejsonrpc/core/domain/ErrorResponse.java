package com.github.arteam.simplejsonrpc.core.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ValueNode;

/**
 * Date: 07.06.14
 * Time: 12:35
 * <p>Representation of a JSON-RPC error response</p>
 */
public class ErrorResponse extends Response {

    @JsonProperty("error")
    private final ErrorMessage error;

    @JsonCreator
    public ErrorResponse(@JsonProperty("id") ValueNode id,
                         @JsonProperty("error") ErrorMessage error) {
        super(id);
        this.error = error;
    }

    public ErrorResponse(ErrorMessage error) {
        super(NullNode.getInstance());
        this.error = error;
    }

    public ErrorMessage getError() {
        return error;
    }
}
