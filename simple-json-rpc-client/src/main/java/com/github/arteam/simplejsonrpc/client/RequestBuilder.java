package com.github.arteam.simplejsonrpc.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.*;
import com.github.arteam.simplejsonrpc.core.domain.ErrorResponse;
import com.github.arteam.simplejsonrpc.core.domain.Request;
import com.github.arteam.simplejsonrpc.core.domain.Response;
import com.github.arteam.simplejsonrpc.core.domain.SuccessResponse;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * Date: 8/9/14
 * Time: 9:04 PM
 *
 * @author Artem Prigoda
 */
public class RequestBuilder<T> {

    @NotNull
    private Transport transport;

    @NotNull
    private ObjectMapper mapper;

    @NotNull
    private String method;

    @NotNull
    private ValueNode id;

    @NotNull
    private ObjectNode objectParams;

    @NotNull
    private ArrayNode arrayParams;

    public RequestBuilder(@NotNull Transport transport, @NotNull ObjectMapper mapper) {
        this.transport = transport;
        this.mapper = mapper;
        id = NullNode.instance;
        objectParams = mapper.createObjectNode();
        arrayParams = mapper.createArrayNode();
        method = "";
    }

    private RequestBuilder(@NotNull Transport transport, @NotNull ObjectMapper mapper, @NotNull String method,
                           @NotNull ValueNode id, @NotNull ObjectNode objectParams, @NotNull ArrayNode arrayParams) {
        this.transport = transport;
        this.mapper = mapper;
        this.method = method;
        this.id = id;
        this.objectParams = objectParams;
        this.arrayParams = arrayParams;
    }

    @NotNull
    public RequestBuilder<T> id(@NotNull Long id) {
        return new RequestBuilder<T>(transport, mapper, method, new LongNode(id), objectParams, arrayParams);
    }

    @NotNull
    public RequestBuilder<T> id(@NotNull Integer id) {
        return new RequestBuilder<T>(transport, mapper, method, new IntNode(id), objectParams, arrayParams);
    }

    @NotNull
    public RequestBuilder<T> id(@NotNull String id) {
        return new RequestBuilder<T>(transport, mapper, method, new TextNode(id), objectParams, arrayParams);
    }

    @NotNull
    public RequestBuilder<T> method(@NotNull String method) {
        this.method = method;
        return new RequestBuilder<T>(transport, mapper, method, id, objectParams, arrayParams);
    }

    @NotNull
    public RequestBuilder<T> param(@NotNull String name, @NotNull Object value) {
        objectParams.set(name, mapper.valueToTree(value));
        return new RequestBuilder<T>(transport, mapper, method, id, objectParams, arrayParams);
    }

    @NotNull
    public RequestBuilder<T> param(int index, @NotNull Object value) {
        arrayParams.insert(index, mapper.valueToTree(value));
        return new RequestBuilder<T>(transport, mapper, method, id, objectParams, arrayParams);
    }

    @NotNull
    public <NT> RequestBuilder<NT> responseType(@NotNull Class<NT> responseType) {
        return new RequestBuilder<NT>(transport, mapper, method, id, objectParams, arrayParams);
    }

    @SuppressWarnings("unchecked")
    public T execute() {
        if (method.isEmpty()) {
            throw new IllegalStateException("Method is not set");
        }
        Request request = new Request("2.0", method, params(), id);
        try {
            String textRequest = mapper.writeValueAsString(request);
            String textResponse = transport.pass(textRequest);

            JsonNode responseNode = mapper.readTree(textResponse);
            JsonNode result = responseNode.get("result");
            JsonNode error = responseNode.get("error");
            if (result != null) {
                SuccessResponse response = mapper.treeToValue(responseNode, SuccessResponse.class);
                return (T) response.getResult();
            } else if (error != null) {
                ErrorResponse errorResponse = mapper.treeToValue(responseNode, ErrorResponse.class);
                throw new JsonRpcException(errorResponse.getError());
            } else {
                throw new JsonMappingException(textResponse + " isn't JSON-RPC response");
            }
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Error in JSON processing", e);
        } catch (IOException e) {
            throw new IllegalStateException("I/O error", e);
        }
    }

    @NotNull
    private JsonNode params() {
        if (objectParams.size() > 0 && arrayParams.size() > 0) {
            throw new IllegalStateException("Both object and array params are set");
        } else if (objectParams.size() > 0 && arrayParams.size() == 0) {
            return objectParams;
        } else if (objectParams.size() == 0 && arrayParams.size() > 0) {
            return arrayParams;
        } else {
            return NullNode.instance;
        }
    }
}
