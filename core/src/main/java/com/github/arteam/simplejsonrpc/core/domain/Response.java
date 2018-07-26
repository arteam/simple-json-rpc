package com.github.arteam.simplejsonrpc.core.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ValueNode;
import org.jetbrains.annotations.NotNull;

/**
 * Date: 07.06.14
 * Time: 12:34
 * <p/>
 * Base representation of a JSON-RPC response (success or error)
 */
public class Response {

    private static final String VERSION = "2.0";

    @NotNull
    @JsonProperty("jsonrpc")
    private final String jsonrpc;

    @NotNull
    @JsonProperty("id")
    private final ValueNode id;

    public Response(@NotNull ValueNode id) {
        this.id = id;
        jsonrpc = VERSION;
    }

    public Response(@NotNull ValueNode id, @NotNull String jsonrpc) {
        this.id = id;
        this.jsonrpc = jsonrpc;
    }

    @NotNull
    public String getJsonrpc() {
        return jsonrpc;
    }

    @NotNull
    public ValueNode getId() {
        return id;
    }
}
