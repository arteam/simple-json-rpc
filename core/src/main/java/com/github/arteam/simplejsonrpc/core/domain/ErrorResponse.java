package com.github.arteam.simplejsonrpc.core.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ValueNode;

import java.util.Objects;

/**
 * Date: 07.06.14
 * Time: 12:35
 * <p>Representation of a JSON-RPC error response</p>
 */
public class ErrorResponse implements Response {

    final public static String VERSION = "2.0";

    private ValueNode id;
    private ErrorMessage error;
    private String jsonrpc;

    public ErrorResponse(){}

    /**
     *
     */
    public ErrorResponse(ValueNode id,
                         ErrorMessage error,
                         String jsonrpc) {
        this.id = id;
        this.error = error;
        this.jsonrpc = jsonrpc;
    }

    public static ErrorResponse of(ErrorMessage error) {
        return new ErrorResponse(NullNode.getInstance(), error, VERSION);
    }

    public static ErrorResponse of(ValueNode id, ErrorMessage error) {
        return new ErrorResponse(id, error, VERSION);
    }

    @JsonProperty @Override public ValueNode id() {
        return id;
    }

    @JsonProperty public ErrorMessage error() {
        return error;
    }

    @JsonProperty @Override public String jsonrpc() {
        return jsonrpc;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        ErrorResponse that = (ErrorResponse) obj;
        return Objects.equals(this.id, that.id) &&
                Objects.equals(this.error, that.error) &&
                Objects.equals(this.jsonrpc, that.jsonrpc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, error, jsonrpc);
    }

    @Override
    public String toString() {
        return "ErrorResponse[" +
                "id=" + id + ", " +
                "error=" + error + ", " +
                "jsonrpc=" + jsonrpc + ']';
    }

}
