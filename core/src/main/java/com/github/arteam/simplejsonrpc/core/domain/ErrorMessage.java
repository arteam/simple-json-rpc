package com.github.arteam.simplejsonrpc.core.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    @Nullable
    private final JsonNode data;
    
    public ErrorMessage(@JsonProperty("code") int code,
                        @JsonProperty("message") @NotNull String message,
                        @JsonProperty("data") @Nullable JsonNode data) {
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

    @Nullable
    public JsonNode getData() {
        return data;
    }

    @Override
    public String toString() {
        return "ErrorMessage{code=" + code + ", message=" + message + ", data=" + data + "}";
    }
}
