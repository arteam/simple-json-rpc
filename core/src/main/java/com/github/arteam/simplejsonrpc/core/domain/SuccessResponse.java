package com.github.arteam.simplejsonrpc.core.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ValueNode;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Date: 07.06.14
 * Time: 12:31
 * Representation of a successful JSON-RPC response
 */
public class SuccessResponse implements Response {
    final public static String VERSION = "2.0";

    private ValueNode id;
    private @Nullable Object result;
    private String jsonrpc;

    public SuccessResponse(){}

    public SuccessResponse(ValueNode id,
                           @Nullable Object result,
                           String jsonrpc) {
        this.id = id;
        this.result = result;
        this.jsonrpc = jsonrpc;
    }

    @JsonProperty @Override
    public ValueNode id() {
        return id;
    }

    @JsonProperty
    public @Nullable Object result() {
        return result;
    }

    @JsonProperty @Override
    public String jsonrpc() {
        return jsonrpc;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        SuccessResponse that = (SuccessResponse) obj;
        return Objects.equals(this.id, that.id) &&
                Objects.equals(this.result, that.result) &&
                Objects.equals(this.jsonrpc, that.jsonrpc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, result, jsonrpc);
    }

    @Override
    public String toString() {
        return "SuccessResponse[" +
                "id=" + id + ", " +
                "result=" + result + ", " +
                "jsonrpc=" + jsonrpc + ']';
    }

}
