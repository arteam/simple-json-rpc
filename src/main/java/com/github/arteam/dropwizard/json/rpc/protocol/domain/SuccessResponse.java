package com.github.arteam.dropwizard.json.rpc.protocol.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ValueNode;

import javax.annotation.Nullable;

/**
 * Date: 07.06.14
 * Time: 12:31
 * JSON-RPC response
 *
 * @author Artem Prigoda
 */
public class SuccessResponse extends Response {

    @Nullable
    @JsonProperty("result")
    private final Object result;

    public SuccessResponse(ValueNode id, @Nullable Object result) {
        super(id);
        this.result = result;
    }
}
