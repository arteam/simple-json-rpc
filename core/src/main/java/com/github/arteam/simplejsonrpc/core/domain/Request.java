package com.github.arteam.simplejsonrpc.core.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Date: 07.06.14
 * Time: 12:24
 * <p>Representation of a JSON-RPC request</p>
 */
public class Request {
    private @Nullable String jsonrpc;
    private @Nullable String method;
    private @Nullable JsonNode params;
    private @Nullable ValueNode id;

    public Request(){}

    /**
     *
     */
    public Request(@Nullable String jsonrpc, @Nullable String method,
                   @Nullable JsonNode params,
                   @Nullable ValueNode id) {
        this.jsonrpc = jsonrpc;
        this.method = method;
        this.params = params;
        this.id = id;
    }

    @JsonProperty
    public ValueNode id() {
        return id != null ? id : NullNode.getInstance();
    }

    @JsonProperty
    public JsonNode params() {
        return params != null ? params : NullNode.getInstance();
    }

    @JsonProperty
    public @Nullable String jsonrpc() {
        return jsonrpc;
    }

    @JsonProperty
    public @Nullable String method() {
        return method;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        Request that = (Request) obj;
        return Objects.equals(this.jsonrpc, that.jsonrpc) &&
                Objects.equals(this.method, that.method) &&
                Objects.equals(this.params, that.params) &&
                Objects.equals(this.id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(jsonrpc, method, params, id);
    }

    @Override
    public String toString() {
        return "Request[" +
                "jsonrpc=" + jsonrpc + ", " +
                "method=" + method + ", " +
                "params=" + params + ", " +
                "id=" + id + ']';
    }

}
