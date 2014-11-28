package com.github.arteam.simplejsonrpc.core.domain;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.NotNull;

/**
 * Date: 07.06.14
 * Time: 12:34
 * <p/>
 * Base representation of a JSON-RPC response (success or error)
 *
 * @author Artem Prigoda
 */
public class Response {

    private static final String VERSION = "2.0";

    @NotNull
    private final String jsonrpc;

    @NotNull
    private final JsonElement id;

    public Response(@NotNull JsonElement id) {
        this.id = id;
        jsonrpc = VERSION;
    }

    public Response(@NotNull JsonPrimitive id, @NotNull String jsonrpc) {
        this.id = id;
        this.jsonrpc = jsonrpc;
    }

    @NotNull
    public String getJsonrpc() {
        return jsonrpc;
    }

    @NotNull
    public JsonElement getId() {
        return id;
    }
}
