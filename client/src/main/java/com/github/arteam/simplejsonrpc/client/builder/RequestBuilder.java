package com.github.arteam.simplejsonrpc.client.builder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.github.arteam.simplejsonrpc.client.Transport;
import com.github.arteam.simplejsonrpc.client.exception.JsonRpcException;
import com.github.arteam.simplejsonrpc.core.domain.ErrorMessage;
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
 * <p> Type-safe builder of JSON-RPC requests.</p>
 * <p> It introduces fluent API to build a request, set an expected response type and perform the request.
 * Builder is immutable: every mutation creates a new object, so it's safe to use
 * in multi-threaded environment.</p>
 * <p> It delegates JSON processing to Jackson {@link ObjectMapper} and actual request performing
 * to {@link Transport}.</p>
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
    private final ValueNode id;

    /**
     * JSON-RPC request params as a map
     */
    @NotNull
    private final ObjectNode objectParams;

    /**
     * JSON-RPC request params as an array
     */
    @NotNull
    private final ArrayNode arrayParams;

    /**
     * Generic type for representing expected response type
     */
    @NotNull
    private final JavaType javaType;

    /**
     * Creates a new default request builder without actual parameters
     *
     * @param transport transport for request performing
     * @param mapper    mapper for JSON processing
     */
    public RequestBuilder(@NotNull Transport transport, @NotNull ObjectMapper mapper) {
        super(transport, mapper);
        id = NullNode.instance;
        objectParams = mapper.createObjectNode();
        arrayParams = mapper.createArrayNode();
        method = "";
        javaType = TypeFactory.defaultInstance().constructType(Object.class);
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
    private RequestBuilder(@NotNull Transport transport, @NotNull ObjectMapper mapper, @NotNull String method,
                           @NotNull ValueNode id, @NotNull ObjectNode objectParams, @NotNull ArrayNode arrayParams,
                           @NotNull JavaType javaType) {
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
        return new RequestBuilder<T>(transport, mapper, method, new LongNode(id), objectParams, arrayParams, javaType);
    }

    /**
     * Sets a request id as an integer value
     *
     * @param id a request id
     * @return new builder
     */
    @NotNull
    public RequestBuilder<T> id(@NotNull Integer id) {
        return new RequestBuilder<T>(transport, mapper, method, new IntNode(id), objectParams, arrayParams, javaType);
    }

    /**
     * Sets a request id as a string value
     *
     * @param id a request id
     * @return new builder
     */
    @NotNull
    public RequestBuilder<T> id(@NotNull String id) {
        return new RequestBuilder<T>(transport, mapper, method, new TextNode(id), objectParams, arrayParams, javaType);
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
     * <p><i>Caution:</i> If you set request parameters this way, you should follow this convention
     * during all the building process like that:</p>
     * <pre>{@code
     * client.createRequest()
     *       .method("find")
     *       .id(43121)
     *       .param("firstName", "Steven")
     *       .param("lastName", "Stamkos")
     *       .returnAs(Player.class)
     *       .execute();}
     * </pre>
     * <p><b>Calls to <i>params</i> method are not permitted after this method has been invoked</b></p>
     *
     * @param name  parameter name
     * @param value parameter value
     * @return new builder
     */
    @NotNull
    public RequestBuilder<T> param(@NotNull String name, @NotNull Object value) {
        ObjectNode newObjectParams = objectParams.deepCopy();
        newObjectParams.set(name, mapper.valueToTree(value));
        return new RequestBuilder<T>(transport, mapper, method, id, newObjectParams, arrayParams, javaType);
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
                TypeFactory.defaultInstance().constructType(responseType));
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
                mapper.getTypeFactory().constructCollectionType(List.class, elementType));
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
                mapper.getTypeFactory().constructCollectionType(Set.class, elementType));
    }

    /**
     * Sets expected return type as a collection of objects.
     * This method is suitable for non-standard collections like {@link java.util.Queue}
     *
     * @param collectionType type of a collection
     * @param elementType    type of elements of a collection
     * @param <E>            generic collection type
     * @return new builder
     */
    @NotNull
    public <E> RequestBuilder<Collection<E>> returnAsCollection(@NotNull Class<? extends Collection> collectionType,
                                                                @NotNull Class<E> elementType) {
        return new RequestBuilder<Collection<E>>(transport, mapper, method, id, objectParams, arrayParams,
                mapper.getTypeFactory().constructCollectionType(collectionType, elementType));
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
                mapper.getTypeFactory().constructArrayType(elementType));
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
                mapper.getTypeFactory().constructMapType(mapClass, String.class, valueType));
    }

    /**
     * Sets expected return type as a generic type, e.g. Guava Optional.
     * Generic type is set as a type reference like that:
     * <pre> {@code new TypeReference<Optional<String>>() {} }</pre>
     *
     * @param tr   type reference
     * @param <NT> a generic type
     * @return new builder
     */
    @NotNull
    public <NT> RequestBuilder<NT> returnAs(@NotNull TypeReference<NT> tr) {
        return new RequestBuilder<NT>(transport, mapper, method, id, objectParams, arrayParams,
                mapper.getTypeFactory().constructType(tr.getType()));
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

    @Nullable
    @SuppressWarnings("unchecked")
    private T executeAndConvert() {
        String textResponse = executeRequest();

        try {
            JsonNode responseNode = mapper.readTree(textResponse);
            JsonNode result = responseNode.get(RESULT);
            JsonNode error = responseNode.get(ERROR);
            JsonNode version = responseNode.get(JSONRPC);
            JsonNode id = responseNode.get(ID);

            if (version == null) {
                throw new IllegalStateException("Not a JSON-RPC response: " + responseNode);
            }
            if (!version.asText().equals(VERSION_2_0)) {
                throw new IllegalStateException("Bad protocol version in a response: " + responseNode);
            }
            if (id == null) {
                throw new IllegalStateException("Unspecified id in a response: " + responseNode);
            }

            if (error == null) {
                if (result != null) {
                    return mapper.convertValue(result, javaType);
                } else {
                    throw new IllegalStateException("Neither result or error is set in a response: " + responseNode);
                }
            } else {
                ErrorMessage errorMessage = mapper.treeToValue(error, ErrorMessage.class);
                throw new JsonRpcException(errorMessage);
            }
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Unable parse a JSON response: " + textResponse, e);
        }
    }

    String executeRequest() {
        ObjectNode requestNode = request(id, method, params());
        String textRequest;
        String textResponse;
        try {
            textRequest = mapper.writeValueAsString(requestNode);
        } catch (JsonProcessingException e) {
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
    private JsonNode params() {
        if (objectParams.size() > 0) {
            if (arrayParams.size() > 0) {
                throw new IllegalArgumentException("Both object and array params are set");
            }
            return objectParams;
        }
        return arrayParams;
    }
}
