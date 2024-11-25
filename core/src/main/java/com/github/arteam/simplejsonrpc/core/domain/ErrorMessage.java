package com.github.arteam.simplejsonrpc.core.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Date: 07.06.14
 * Time: 15:16
 * <p>Representation of a JSON-RPC error message</p>
 */
public class ErrorMessage {
    private int code;
    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private @Nullable JsonNode data;

    public ErrorMessage(){}

    public ErrorMessage(@JsonProperty("code") int code,
                        @JsonProperty("message") String message,
                        @JsonProperty("data") @Nullable JsonNode data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    @Override public String toString() {
        return "ErrorMessage{code=" + code + ", message=" + message + ", data=" + data + "}";
    }

    public int getCode() {return code;}
    @JsonProperty("code") public int code() {
        return code;
    }

    public String getMessage() {return message;}
    @JsonProperty("message") public String message() {
        return message;
    }

    @JsonProperty("data")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public @Nullable JsonNode data() {return data;}
    @Nullable public JsonNode getData() {
        return data;
    }

    @Override public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        ErrorMessage that = (ErrorMessage) obj;
        return this.code == that.code &&
                Objects.equals(this.message, that.message) &&
                Objects.equals(this.data, that.data);
    }

    @Override public int hashCode() {
        return Objects.hash(code, message, data);
    }

}