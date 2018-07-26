package com.github.arteam.simplejsonrpc.core.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * Date: 07.06.14
 * Time: 12:24
 * <p/>
 * Representation of a JSON-RPC request
 */
public class Request {

    @Nullable
    private final String jsonrpc;

    @Nullable
    private final String method;

    @NotNull
    private final JsonNode params;

    @NotNull
    private final ValueNode id;

    public Request(@JsonProperty("jsonrpc") @Nullable String jsonrpc,
                   @JsonProperty("method") @Nullable String method,
                   @JsonProperty("params") @NotNull JsonNode params,
                   @JsonProperty("id") @NotNull ValueNode id) {
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
        return id;
    }

    @NotNull
    public JsonNode getParams() {
        return params;
    }

    @Override
    public String toString() {
        return "Request{jsonrpc=" + jsonrpc + ", method=" + method + ", id=" + id + ", params=" + params + "}";
    }
}
