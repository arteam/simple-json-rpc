package com.github.arteam.simplejsonrpc.core.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ContainerNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import com.google.common.base.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * Date: 07.06.14
 * Time: 12:24
 * <p/>
 * Representation of a JSON-RPC request
 *
 * @author Artem Prigoda
 */
public class Request {

    @Nullable
    private final String jsonrpc;

    @Nullable
    private final String method;

    @NotNull
    private final ValueNode id;

    @NotNull
    private final JsonNode params;

    public Request(@JsonProperty("jsonrpc") @Nullable String jsonrpc,
                   @JsonProperty("method") @Nullable String method,
                   @JsonProperty("id") @NotNull ValueNode id,
                   @JsonProperty("params") @NotNull JsonNode params) {
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
        return Objects.toStringHelper(this)
                .add("jsonrpc", jsonrpc)
                .add("method", method)
                .add("id", id)
                .add("params", params)
                .toString();
    }
}
