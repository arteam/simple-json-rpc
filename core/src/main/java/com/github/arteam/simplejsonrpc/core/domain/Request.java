package com.github.arteam.simplejsonrpc.core.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * Date: 07.06.14
 * Time: 12:24
 * <p>Representation of a JSON-RPC request</p>
 */
public class Request {

    @Nullable
    private final String jsonrpc;

    @Nullable
    private final String method;

    @Nullable
    private final JsonNode params;

    @Nullable
    private final ValueNode id;

    public Request(@JsonProperty("jsonrpc") @Nullable String jsonrpc,
                   @JsonProperty("method") @Nullable String method,
                   @JsonProperty("params") @Nullable JsonNode params,
                   @JsonProperty("id") @Nullable ValueNode id) {
        this.jsonrpc = jsonrpc;
        this.method = method;
        this.id = id;
        this.params = params;
    }

    @Nullable
    public String getJsonrpc() {
        return jsonrpc;
    }

    @Nullable
    public String getMethod() {
        return method;
    }

    @NotNull
    public ValueNode getId() {
        return id != null ? id : NullNode.getInstance();
    }

    @NotNull
    public JsonNode getParams() {
        return params != null ? params : NullNode.getInstance();
    }

    @Override
    public String toString() {
        return "Request{jsonrpc=" + jsonrpc + ", method=" + method + ", id=" + id + ", params=" + params + "}";
    }
}
