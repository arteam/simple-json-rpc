package com.github.arteam.simplejsonrpc.core.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import org.jetbrains.annotations.Nullable;

/**
 * Date: 07.06.14
 * Time: 12:24
 * <p>Representation of a JSON-RPC request</p>
 */
public record Request(@Nullable String jsonrpc, @Nullable String method,
                      @Nullable JsonNode params,
                      @Nullable ValueNode id) {

    public ValueNode id() {
        return id != null ? id : NullNode.getInstance();
    }

    public JsonNode params() {
        return params != null ? params : NullNode.getInstance();
    }

}
