package com.github.arteam.simplejsonrpc.core.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import org.jetbrains.annotations.Nullable;

/**
 * Date: 07.06.14
 * Time: 15:16
 * <p>Representation of a JSON-RPC error message</p>
 */
public record ErrorMessage(@JsonProperty("code") int code, @JsonProperty("message") String message,
                           @JsonProperty("data") @JsonInclude(JsonInclude.Include.NON_NULL) @Nullable JsonNode data) {

    public ErrorMessage(@JsonProperty("code") int code,
                        @JsonProperty("message") String message,
                        @JsonProperty("data") @Nullable JsonNode data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    @Nullable
    public JsonNode getData() {
        return data;
    }

    @Override
    public String toString() {
        return "ErrorMessage{code=" + code + ", message=" + message + ", data=" + data + "}";
    }
}
