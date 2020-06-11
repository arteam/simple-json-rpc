package com.github.arteam.simplejsonrpc.core.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.jetbrains.annotations.NotNull;

/**
 * Date: 07.06.14
 * Time: 15:16
 * <p/>
 * Representation of a JSON-RPC error message
 */
public class ErrorMessage {

    @JsonProperty("code")
    private final int code;

    @NotNull
    @JsonProperty("message")
    private final String message;
    
    @JsonProperty("data")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final JsonNode data;
    
    public ErrorMessage(@JsonProperty("code") int code,
                        @JsonProperty("message") @NotNull String message,
                        @JsonProperty("data") @NotNull JsonNode data) {
        this.code = code;
        this.message = message;
        this.data = data;
	}

    public int getCode() {
        return code;
    }

    @NotNull
    public String getMessage() {
        return message;
    }
    
    public JsonNode getData() {
        return data;
    }

    @Override
    public String toString() {
        return "ErrorMessage{code=" + code + ", message=" + message + ", data=" + data + "}";
    }
}
