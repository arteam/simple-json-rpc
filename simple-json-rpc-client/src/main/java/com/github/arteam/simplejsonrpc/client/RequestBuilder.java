package com.github.arteam.simplejsonrpc.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.*;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.SimpleType;
import com.github.arteam.simplejsonrpc.core.domain.ErrorMessage;
import com.github.arteam.simplejsonrpc.core.domain.ErrorResponse;
import com.github.arteam.simplejsonrpc.core.domain.Request;
import com.github.arteam.simplejsonrpc.core.domain.SuccessResponse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Date: 8/9/14
 * Time: 9:04 PM
 *
 * @author Artem Prigoda
 */
public class RequestBuilder<T> {

    public static final String VERSION_2_0 = "2.0";

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

    @NotNull
    private JavaType javaType;

    public RequestBuilder(@NotNull Transport transport, @NotNull ObjectMapper mapper) {
        this.transport = transport;
        this.mapper = mapper;
        id = NullNode.instance;
        objectParams = mapper.createObjectNode();
        arrayParams = mapper.createArrayNode();
        method = "";
    }

    private RequestBuilder(@NotNull Transport transport, @NotNull ObjectMapper mapper, @NotNull String method,
                           @NotNull ValueNode id, @NotNull ObjectNode objectParams, @NotNull ArrayNode arrayParams,
                           @NotNull JavaType javaType) {
        this.transport = transport;
        this.mapper = mapper;
        this.method = method;
        this.id = id;
        this.objectParams = objectParams;
        this.arrayParams = arrayParams;
        this.javaType = javaType;
    }

    @NotNull
    public RequestBuilder<T> id(@NotNull Long id) {
        return new RequestBuilder<T>(transport, mapper, method, new LongNode(id), objectParams, arrayParams, javaType);
    }

    @NotNull
    public RequestBuilder<T> id(@NotNull Integer id) {
        return new RequestBuilder<T>(transport, mapper, method, new IntNode(id), objectParams, arrayParams, javaType);
    }

    @NotNull
    public RequestBuilder<T> id(@NotNull String id) {
        return new RequestBuilder<T>(transport, mapper, method, new TextNode(id), objectParams, arrayParams, javaType);
    }

    @NotNull
    public RequestBuilder<T> method(@NotNull String method) {
        return new RequestBuilder<T>(transport, mapper, method, id, objectParams, arrayParams, javaType);
    }

    @NotNull
    public RequestBuilder<T> param(@NotNull String name, @NotNull Object value) {
        ObjectNode newObjectParams = objectParams.deepCopy();
        newObjectParams.set(name, mapper.valueToTree(value));
        return new RequestBuilder<T>(transport, mapper, method, id, newObjectParams, arrayParams, javaType);
    }

    @NotNull
    public RequestBuilder<T> params(@NotNull Object... values) {
        ArrayNode newArrayParams = mapper.createArrayNode();
        for (Object value : values) {
            newArrayParams.add(mapper.valueToTree(value));
        }
        return new RequestBuilder<T>(transport, mapper, method, id, objectParams, newArrayParams, javaType);
    }

    @NotNull
    public <NT> RequestBuilder<NT> returnAs(@NotNull Class<NT> responseType) {
        return new RequestBuilder<NT>(transport, mapper, method, id, objectParams, arrayParams,
                SimpleType.construct(responseType));
    }

    @NotNull
    public <E> RequestBuilder<List<E>> returnAsList(@NotNull Class<E> elementType) {
        return new RequestBuilder<List<E>>(transport, mapper, method, id, objectParams, arrayParams,
                mapper.getTypeFactory().constructCollectionType(List.class, elementType));
    }

    @NotNull
    public <E> RequestBuilder<Set<E>> returnAsSet(@NotNull Class<E> elementType) {
        return new RequestBuilder<Set<E>>(transport, mapper, method, id, objectParams, arrayParams,
                mapper.getTypeFactory().constructCollectionType(Set.class, elementType));
    }

    @NotNull
    public <E> RequestBuilder<Collection<E>> returnAsCollection(@NotNull Class<? extends Collection> collectionType,
                                                         @NotNull Class<E> elementType) {
        return new RequestBuilder<Collection<E>>(transport, mapper, method, id, objectParams, arrayParams,
                mapper.getTypeFactory().constructCollectionType(collectionType, elementType));
    }

    @NotNull
    public <E> RequestBuilder<E[]> returnAsArray(@NotNull Class<E> elementType) {
        return new RequestBuilder<E[]>(transport, mapper, method, id, objectParams, arrayParams,
                mapper.getTypeFactory().constructArrayType(elementType));
    }

    @NotNull
    public <K, V> RequestBuilder<Map<K, V>> returnAsMap(@NotNull Class<K> keyType, @NotNull Class<V> valueType) {
        return new RequestBuilder<Map<K, V>>(transport, mapper, method, id, objectParams, arrayParams,
                mapper.getTypeFactory().constructMapType(Map.class, keyType, valueType));
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public T execute() {
        if (method.isEmpty()) {
            throw new IllegalStateException("Method is not set");
        }
        Request request = new Request(VERSION_2_0, method, params(), id);
        try {
            String textRequest = mapper.writeValueAsString(request);
            String textResponse = transport.pass(textRequest);

            JsonNode responseNode = mapper.readTree(textResponse);
            JsonNode result = responseNode.get("result");
            JsonNode error = responseNode.get("error");
            JsonNode version = responseNode.get("jsonrpc");
            JsonNode id = responseNode.get("id");

            if (version == null || !version.asText().equals(VERSION_2_0)) {
                throw new IllegalStateException("Bad protocol version in response: " + responseNode);
            }
            if (id == null) {
                throw new IllegalStateException("Unspecified id in response: " + responseNode);
            }

            if (result != null && error == null) {
                return mapper.convertValue(result, javaType);
            } else {
                ErrorMessage errorMessage = mapper.treeToValue(error, ErrorMessage.class);
                throw new JsonRpcException(errorMessage);
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
