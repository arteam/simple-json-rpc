package com.github.arteam.dropwizard.json.rpc.protocol.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ContainerNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import com.google.common.base.Objects;

import javax.annotation.Nullable;

/**
 * Date: 07.06.14
 * Time: 12:24
 * JSON-RPC request
 *
 * @author Artem Prigoda
 */
public class Request {

    @Nullable
    private final String jsonrpc;

    @Nullable
    private final String method;

    @Nullable
    private final ValueNode id;

    @Nullable
    private final ContainerNode params;

    public Request(@JsonProperty("jsonrpc") @Nullable String jsonrpc,
                   @JsonProperty("method") @Nullable String method,
                   @JsonProperty("id") @Nullable ValueNode id,
                   @JsonProperty("params") @Nullable ContainerNode params) {
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

    @Nullable
    public ValueNode getId() {
        return id;
    }

    @Nullable
    public ContainerNode getParams() {
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
