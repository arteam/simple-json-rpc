package com.github.arteam.simplejsonrpc.core.domain;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * Date: 07.06.14
 * Time: 12:24
 * <p/>
 * Representation of a JSON-RPC request
 *
 * @author Artem Prigoda
 */
public class Request {

    @Nullable
    private final String jsonrpc;

    @Nullable
    private final String method;

    @Nullable
    private final JsonElement params;

    @Nullable
    private final JsonElement id;

    public Request(@Nullable String jsonrpc, @Nullable String method,
                   @Nullable JsonElement params, @Nullable JsonElement id) {
        this.jsonrpc = jsonrpc;
        this.method = method;
        this.id = id;
        this.params = params;
    }

    @Nullable
    public String getJsonrpc() {
        return jsonrpc;
    }

    @Nullable
    public String getMethod() {
        return method;
    }

    @NotNull
    public JsonElement getId() {
        return id != null ? id : JsonNull.INSTANCE;
    }

    @NotNull
    public JsonElement getParams() {
        return params != null ? params : JsonNull.INSTANCE;
    }

    @Override
    public String toString() {
        return "Request{jsonrpc=" + jsonrpc + ", method=" + method + ", id=" + id + ", params=" + params + "}";
    }
}
