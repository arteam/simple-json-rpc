package com.github.arteam.simplejsonrpc.client.builder;

import com.github.arteam.simplejsonrpc.client.Transport;
import com.github.arteam.simplejsonrpc.client.exception.JsonRpcException;
import com.github.arteam.simplejsonrpc.core.domain.ErrorMessage;
import com.google.gson.*;
import com.google.gson.internal.$Gson$Types;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Date: 8/9/14
 * Time: 9:04 PM
 * <p/>
 * Type-safe builder of JSON-RPC requests.
 * <p/>
 * It introduces fluent API to build a request, set an expected response type and perform the request.
 * Builder is immutable: every mutation creates a new object, so it's safe to use in multi-threaded environment.
 * <p/>
 * It delegates JSON processing to Jackson {@link Gson} and actual request performing to {@link com.github.arteam.simplejsonrpc.client.Transport}.
 *
 * @author Artem Prigoda
 */
public class RequestBuilder<T> extends AbstractBuilder {

    /**
     * JSON-RPC request method
     */
    @NotNull
    private final String method;

    /**
     * JSON-RPC request id
     */
    @NotNull
    private final JsonElement id;

    /**
     * JSON-RPC request params as a map
     */
    @NotNull
    private final JsonObject objectParams;

    /**
     * JSON-RPC request params as an array
     */
    @NotNull
    private final JsonArray arrayParams;

    /**
     * Generic type for representing expected response type
     */
    @NotNull
    private final Type javaType;

    /**
     * Creates a new default request builder without actual parameters
     *
     * @param transport transport for request performing
     * @param mapper    mapper for JSON processing
     */
    public RequestBuilder(@NotNull Transport transport, @NotNull Gson mapper) {
        super(transport, mapper);
        id = JsonNull.INSTANCE;
        objectParams = new JsonObject();
        arrayParams = new JsonArray();
        method = "";
        javaType = Object.class;
    }

    /**
     * Creates new builder as part of a chain of builders to a full-initialized type-safe builder
     *
     * @param transport    new transport
     * @param mapper       new mapper
     * @param method       new method
     * @param id           new id
     * @param objectParams new object params
     * @param arrayParams  new array params
     * @param javaType     new response type
     */
    private RequestBuilder(@NotNull Transport transport, @NotNull Gson mapper, @NotNull String method,
                           @NotNull JsonElement id, @NotNull JsonObject objectParams, @NotNull JsonArray arrayParams,
                           @NotNull Type javaType) {
        super(transport, mapper);
        this.method = method;
        this.id = id;
        this.objectParams = objectParams;
        this.arrayParams = arrayParams;
        this.javaType = javaType;
    }

    /**
     * Sets a request id as a long value
     *
     * @param id a  request id
     * @return new builder
     */
    @NotNull
    public RequestBuilder<T> id(@NotNull Long id) {
        return new RequestBuilder<T>(transport, mapper, method, new JsonPrimitive(id), objectParams, arrayParams, javaType);
    }

    /**
     * Sets a request id as an integer value
     *
     * @param id a request id
     * @return new builder
     */
    @NotNull
    public RequestBuilder<T> id(@NotNull Integer id) {
        return new RequestBuilder<T>(transport, mapper, method, new JsonPrimitive(id), objectParams, arrayParams, javaType);
    }

    /**
     * Sets a request id as a string value
     *
     * @param id a request id
     * @return new builder
     */
    @NotNull
    public RequestBuilder<T> id(@NotNull String id) {
        return new RequestBuilder<T>(transport, mapper, method, new JsonPrimitive(id), objectParams, arrayParams, javaType);
    }

    /**
     * Sets a request method
     *
     * @param method a request method
     * @return new builder
     */
    @NotNull
    public RequestBuilder<T> method(@NotNull String method) {
        return new RequestBuilder<T>(transport, mapper, method, id, objectParams, arrayParams, javaType);
    }

    /**
     * Adds a new parameter to current request parameters.
     * <p/>
     * <i>Caution:</i> If you set request parameters this way, you should follow this convention
     * during all the building process like that:
     * <pre>
     * client.createRequest()
     *       .method("find")
     *       .id(43121)
     *       .param("firstName", "Steven")
     *       .param("lastName", "Stamkos")
     *       .returnAs(Player.class)
     *       .execute();
     * </pre>
     * <p/>
     * <b>Calls to <i>params</i> method are not permitted after this method has been invoked</b>.
     *
     * @param name  parameter name
     * @param value parameter value
     * @return new builder
     */
    @NotNull
    public RequestBuilder<T> param(@NotNull String name, @NotNull Object value) {
        return param(name, value, value.getClass());
    }

    /**
     * Adds a new complex parameter (e.g. parametrized) to current request parameters.
     *
     * @param name  parameter name
     * @param value parameter value
     * @param type  parameter type
     * @return new builder
     */
    public RequestBuilder<T> param(@NotNull String name, @NotNull Object value, @NotNull Type type) {
        // I think there no much sense of making a defence copy
        JsonElement element = mapper.toJsonTree(value, type);
        objectParams.add(name, element);
        return new RequestBuilder<T>(transport, mapper, method, id, objectParams, arrayParams, javaType);
    }

    /**
     * Sets request parameters to request parameters.
     * Parameters are interpreted according to its positions.
     *
     * @param values array of parameters
     * @return new builder
     */
    @NotNull
    public RequestBuilder<T> params(@NotNull Object... values) {
        return new RequestBuilder<T>(transport, mapper, method, id, objectParams, arrayParams(values), javaType);
    }

    /**
     * Sets expected return type. This method is suitable for non-generic types
     *
     * @param responseType expected return type
     * @param <NT>         new return type
     * @return new builder
     */
    @NotNull
    public <NT> RequestBuilder<NT> returnAs(@NotNull Class<NT> responseType) {
        return new RequestBuilder<NT>(transport, mapper, method, id, objectParams, arrayParams,
                responseType);
    }

    /**
     * Sets expected return type as a list of objects
     *
     * @param elementType type of elements of a list
     * @param <E>         generic list type
     * @return new builder
     */
    @NotNull
    public <E> RequestBuilder<List<E>> returnAsList(@NotNull Class<E> elementType) {
        return new RequestBuilder<List<E>>(transport, mapper, method, id, objectParams, arrayParams,
                $Gson$Types.newParameterizedTypeWithOwner(null, List.class, elementType));
    }

    /**
     * Sets expected return type as a set of objects
     *
     * @param elementType type of elements of a set
     * @param <E>         generic set type
     * @return new builder
     */
    @NotNull
    public <E> RequestBuilder<Set<E>> returnAsSet(@NotNull Class<E> elementType) {
        return new RequestBuilder<Set<E>>(transport, mapper, method, id, objectParams, arrayParams,
                $Gson$Types.newParameterizedTypeWithOwner(null, Set.class, elementType));
    }

    /**
     * Sets expected return type as a collection of objects.
     * This method is suitable for non-standard collections like {@link java.util.Queue}
     *
     * @param elementType type of elements of a collection
     * @param <E>         generic collection type
     * @return new builder
     */
    @NotNull
    public <E> RequestBuilder<Collection<E>> returnAsCollection(@NotNull Class<? extends Collection> collectionType,
                                                                @NotNull Class<E> elementType) {
        return new RequestBuilder<Collection<E>>(transport, mapper, method, id, objectParams, arrayParams,
                $Gson$Types.newParameterizedTypeWithOwner(null, collectionType, elementType));
    }

    /**
     * Sets expected return type as an array
     *
     * @param elementType type of elements of an array
     * @param <E>         generic array type
     * @return new builder
     */
    @NotNull
    public <E> RequestBuilder<E[]> returnAsArray(@NotNull Class<E> elementType) {
        return new RequestBuilder<E[]>(transport, mapper, method, id, objectParams, arrayParams,
                $Gson$Types.arrayOf(elementType));
    }

    /**
     * Sets expected return type as a map of objects.
     * Because JSON type system the map should have strings as keys.
     *
     * @param mapClass  expected map interface or implementation,
     *                  e.g. {@link java.util.Map}, {@link java.util.HashMap}.
     *                  {@link java.util.LinkedHashMap}, {@link java.util.SortedMap}
     * @param valueType map value type
     * @param <V>       generic map value type
     * @return new builder
     */
    @NotNull
    public <V> RequestBuilder<Map<String, V>> returnAsMap(@NotNull Class<? extends Map> mapClass,
                                                          @NotNull Class<V> valueType) {
        return new RequestBuilder<Map<String, V>>(transport, mapper, method, id, objectParams, arrayParams,
                $Gson$Types.newParameterizedTypeWithOwner(null, mapClass, String.class, valueType));
    }

    /**
     * Sets expected return type as a generic type, e.g. Guava Optional.
     * Generic type is set as a type reference like that:
     * <pre> new TypeReference<Optional<String>>() {} </pre>
     *
     * @param tr   type reference
     * @param <NT> a generic type
     * @return new builder
     */
    @NotNull
    public <NT> RequestBuilder<NT> returnAs(@NotNull TypeToken<NT> tr) {
        return new RequestBuilder<NT>(transport, mapper, method, id, objectParams, arrayParams, tr.getType());
    }

    /**
     * Execute a request through {@link Transport} and convert a not null response to an expected type
     *
     * @return expected not null response
     * @throws JsonRpcException      in case of JSON-RPC error, returned by the server
     * @throws IllegalStateException if the response is null
     */
    @NotNull
    public T execute() {
        T result = executeAndConvert();
        if (result == null) {
            throw new IllegalStateException("Response is null. Use 'executeNullable' if this is acceptable");
        }
        return result;
    }

    /**
     * Execute a request through {@link Transport} and convert a nullable response to an expected type
     *
     * @return expected response
     * @throws JsonRpcException in case of JSON-RPC error,  returned by the server
     */
    @Nullable
    public T executeNullable() {
        return executeAndConvert();
    }

    /**
     * Execute a request through {@link Transport} and convert a response to an expected type
     *
     * @return expected response
     * @throws JsonRpcException in case of JSON-RPC error, returned by the server
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public T executeAndConvert() {
        String textResponse = executeRequest();

        JsonElement responseJsonElement = mapper.fromJson(textResponse, JsonElement.class);
        if (!responseJsonElement.isJsonObject()) {
            throw new IllegalStateException("Not a JSON object: " + responseJsonElement);
        }
        JsonObject responseNode = responseJsonElement.getAsJsonObject();
        JsonElement result = responseNode.get(RESULT);
        JsonElement error = responseNode.get(ERROR);
        JsonElement version = responseNode.get(JSONRPC);
        JsonElement id = responseNode.get(ID);

        if (version == null) {
            throw new IllegalStateException("Not a JSON-RPC response: " + responseNode);
        }
        if (!version.getAsString().equals(VERSION_2_0)) {
            throw new IllegalStateException("Bad protocol version in a response: " + responseNode);
        }
        if (id == null) {
            throw new IllegalStateException("Unspecified id in a response: " + responseNode);
        }

        if (error == null) {
            if (result != null) {
                return mapper.fromJson(result, javaType);
            } else {
                throw new IllegalStateException("Neither result or error is set in a response: " + responseNode);
            }
        } else {
            ErrorMessage errorMessage = mapper.fromJson(error, ErrorMessage.class);
            throw new JsonRpcException(errorMessage);
        }
    }

    String executeRequest() {
        JsonObject requestNode = request(id, method, params());
        String textRequest;
        String textResponse;
        try {
            textRequest = mapper.toJson(requestNode);
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable convert " + requestNode + " to JSON", e);
        }
        try {
            textResponse = transport.pass(textRequest);
        } catch (IOException e) {
            throw new IllegalStateException("I/O error during a request processing", e);
        }
        return textResponse;
    }

    @NotNull
    private JsonElement params() {
        if (objectParams.entrySet().size() > 0) {
            if (arrayParams.size() > 0) {
                throw new IllegalArgumentException("Both object and array params are set");
            }
            return objectParams;
        }
        return arrayParams;
    }
}
