package com.github.arteam.simplejsonrpc.client.builder;

import com.github.arteam.simplejsonrpc.client.Transport;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Date: 10/12/14
 * Time: 6:48 PM
 * Abstract builder for JSON-RPC requests
 *
 * @author Artem Prigoda
 */
public class AbstractBuilder {

    // Protocol constants
    protected static final String VERSION_2_0 = "2.0";
    protected static final String RESULT = "result";
    protected static final String ERROR = "error";
    protected static final String JSONRPC = "jsonrpc";
    protected static final String ID = "id";
    protected static final String METHOD = "method";
    protected static final String PARAMS = "params";

    /**
     * Transport for performing a text request and returning a text response
     */
    @NotNull
    protected final Transport transport;

    /**
     * Jackson mapper for JSON processing
     */
    @NotNull
    protected final Gson mapper;

    public AbstractBuilder(@NotNull Transport transport, @NotNull Gson mapper) {
        this.transport = transport;
        this.mapper = mapper;
    }

    /**
     * Builds request params as a JSON array
     *
     * @param values request params
     * @return a new JSON array
     */
    @NotNull
    protected JsonArray arrayParams(@NotNull Object[] values) {
        JsonArray newArrayParams = new JsonArray();
        for (Object value : values) {
            newArrayParams.add(mapper.toJsonTree(value));
        }
        return newArrayParams;
    }

    /**
     * Builds request params as a JSON object
     *
     * @param params request params
     * @return a new JSON object
     */
    @NotNull
    protected JsonObject objectParams(@NotNull Map<String, ?> params) {
        JsonObject objectNode = new JsonObject();
        for (String key : params.keySet()) {
            objectNode.add(key, mapper.toJsonTree(params.get(key)));
        }
        return objectNode;
    }

    /**
     * Creates a new JSON-RPC request as a JSON object
     *
     * @param id     request id
     * @param method request method
     * @param params request params
     * @return a new request as a JSON object
     */
    @NotNull
    protected JsonObject request(@NotNull JsonElement id, @NotNull String method,
                                 @NotNull JsonElement params) {
        if (method.isEmpty()) {
            throw new IllegalArgumentException("Method is not set");
        }
        JsonObject requestNode = new JsonObject();
        requestNode.addProperty(JSONRPC, VERSION_2_0);
        requestNode.addProperty(METHOD, method);
        requestNode.add(PARAMS, params);
        if (!id.isJsonNull()) {
            requestNode.add(ID, id);
        }
        return requestNode;
    }
}
