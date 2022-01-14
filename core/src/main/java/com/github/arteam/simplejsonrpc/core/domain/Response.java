package com.github.arteam.simplejsonrpc.core.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ValueNode;

/**
 * Date: 07.06.14
 * Time: 12:34
 * <p>Base representation of a JSON-RPC response (success or error)</p>
 */
public class Response {

    private static final String VERSION = "2.0";

    @JsonProperty("jsonrpc")
    private final String jsonrpc;

    @JsonProperty("id")
    private final ValueNode id;

    public Response(ValueNode id) {
        this.id = id;
        jsonrpc = VERSION;
    }

    public Response(ValueNode id, String jsonrpc) {
        this.id = id;
        this.jsonrpc = jsonrpc;
    }

    public String getJsonrpc() {
        return jsonrpc;
    }

    public ValueNode getId() {
        return id;
    }
}
