package com.github.arteam.simplejsonrpc.client.builder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.*;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.github.arteam.simplejsonrpc.client.Transport;
import com.github.arteam.simplejsonrpc.client.exception.JsonRpcBatchException;
import com.github.arteam.simplejsonrpc.core.domain.ErrorMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Optional.empty;

/**
 * Date: 10/12/14
 * Time: 6:23 PM
 * <p> Fluent builder of batch JSON-RPC requests</p>
 * <p> It provides facility to create a list of requests, set expected response types,
 * execute the JSON-RPC request and process the JSON-RPC response.</p>
 * <p> Return type is a map of responses (Java objects) by request ids. In cases of errors it throws
 * {@link JsonRpcBatchException} with detailed status of success and failed requests.</p>
 * <p> It delegates JSON processing to Jackson {@link ObjectMapper} and actual request performing to
 * {@link Transport}.</p>
 * <p> The basic pattern is following:</p>
 * <pre>{@code
 * Map<String, Player> result = client.createBatchRequest()
 *      .add("43121", "findByInitials", "Steven", "Stamkos")
 *      .add("43122", "findByInitials", "Jack", "Allen")
 *      .keysType(String.class)
 *      .returnType(Player.class)
 *      .execute();
 * }</pre>
 */
public class BatchRequestBuilder<K, V> extends AbstractBuilder {

    /**
     * List of requests
     */
    @NotNull
    private final List<ObjectNode> requests;

    /**
     * Map of expected return types by request ids
     */
    @NotNull
    private final Map<Object, JavaType> returnTypes;

    /**
     * Type of request ids
     */
    @Nullable
    private final Class<K> keysType;

    /**
     * Expected return type for all requests
     * <p></p>
     * This property works exclusively with {@code returnTypes}. Only one of them should be set.
     */
    @Nullable
    private final JavaType returnType;

    /**
     * Creates a new batch request builder in an initial state
     *
     * @param transport transport for request performing
     * @param mapper    mapper for JSON processing
     */
    public BatchRequestBuilder(@NotNull Transport transport, @NotNull ObjectMapper mapper) {
        this(transport, mapper, new ArrayList<ObjectNode>(), new HashMap<Object, JavaType>(), null, null);
    }

    /**
     * Creates a new batch request builder as a part of a chain
     *
     * @param transport   transport for request performing
     * @param mapper      mapper for JSON processing
     * @param requests    new requests
     * @param returnTypes new return types
     * @param keysType    new key type
     * @param returnType  new values type
     */
    public BatchRequestBuilder(@NotNull Transport transport, @NotNull ObjectMapper mapper,
                               @NotNull List<ObjectNode> requests, @NotNull Map<Object, JavaType> returnTypes,
                               @Nullable Class<K> keysType, @Nullable JavaType returnType) {
        super(transport, mapper);
        this.requests = requests;
        this.returnTypes = returnTypes;
        this.keysType = keysType;
        this.returnType = returnType;
    }

    /**
     * Adds a new request without specifying a return type
     *
     * @param id     request id as a long value
     * @param method request method
     * @param params request params as an array
     * @return the current builder
     */
    @NotNull
    public BatchRequestBuilder<K, V> add(long id, @NotNull String method, @NotNull Object... params) {
        requests.add(request(new LongNode(id), method, arrayParams(params)));
        return this;
    }

    /**
     * Adds a new request without specifying a return type
     *
     * @param id     request id as an int value
     * @param method request method
     * @param params request params as an array
     * @return the current builder
     */
    @NotNull
    public BatchRequestBuilder<K, V> add(int id, @NotNull String method, @NotNull Object... params) {
        requests.add(request(new IntNode(id), method, arrayParams(params)));
        return this;
    }

    /**
     * Adds a new request without specifying a return type
     *
     * @param id     request id as a text value
     * @param method request method
     * @param params request params as an array
     * @return the current builder
     */
    @NotNull
    public BatchRequestBuilder<K, V> add(String id, @NotNull String method, @NotNull Object... params) {
        requests.add(request(new TextNode(id), method, arrayParams(params)));
        return this;
    }

    /**
     * Adds a new notification request without specifying a return type
     *
     * @param method request method
     * @param params request params as an array
     * @return the current builder
     */
    @NotNull
    public BatchRequestBuilder<K, V> add(@NotNull String method, @NotNull Object... params) {
        requests.add(request(NullNode.instance, method, arrayParams(params)));
        return this;
    }

    /**
     * Adds a new request without specifying a return type
     *
     * @param id     request id as a long value
     * @param method request method
     * @param params request params as a map of parameter names to values
     * @return the current builder
     */
    @NotNull
    public BatchRequestBuilder<K, V> add(long id, @NotNull String method, @NotNull Map<String, ?> params) {
        requests.add(request(new LongNode(id), method, objectParams(params)));
        return this;
    }

    /**
     * Adds a new request without specifying a return type
     *
     * @param id     request id as an int value
     * @param method request method
     * @param params request params as a map of parameter names to values
     * @return the current builder
     */
    @NotNull
    public BatchRequestBuilder<K, V> add(int id, @NotNull String method, @NotNull Map<String, ?> params) {
        requests.add(request(new IntNode(id), method, objectParams(params)));
        return this;
    }

    /**
     * Adds a new request without specifying a return type
     *
     * @param id     request id as a text value
     * @param method request method
     * @param params request params as a map of parameter names to values
     * @return the current builder
     */
    @NotNull
    public BatchRequestBuilder<K, V> add(String id, @NotNull String method, @NotNull Map<String, ?> params) {
        requests.add(request(new TextNode(id), method, objectParams(params)));
        return this;
    }

    /**
     * Adds a new notification request without specifying a return type
     *
     * @param method request method
     * @param params request params as an array
     * @return the current builder
     */
    @NotNull
    public BatchRequestBuilder<K, V> add(@NotNull String method, @NotNull Map<String, ?> params) {
        requests.add(request(NullNode.instance, method, objectParams(params)));
        return this;
    }

    /**
     * Adds a new request with a return type
     *
     * @param id           request id as a long value
     * @param method       request method
     * @param params       request params as an array
     * @param responseType expected response type
     * @return the current builder
     */
    @NotNull
    public BatchRequestBuilder<K, V> add(long id, @NotNull String method, @NotNull Object[] params,
                                         @NotNull Class<?> responseType) {
        return add(id, method, params).returnType(id, responseType);
    }

    /**
     * Adds a new request with a return type
     *
     * @param id           request id as an int value
     * @param method       request method
     * @param params       request params as an array
     * @param responseType expected response type
     * @return the current builder
     */
    @NotNull
    public BatchRequestBuilder<K, V> add(int id, @NotNull String method, @NotNull Object[] params,
                                         @NotNull Class<?> responseType) {
        return add(id, method, params).returnType(id, responseType);
    }

    /**
     * Adds a new request with a return type
     *
     * @param id           request id as a text value
     * @param method       request method
     * @param params       request params as an array
     * @param responseType expected response type
     * @return the current builder
     */
    @NotNull
    public BatchRequestBuilder<K, V> add(String id, @NotNull String method, @NotNull Object[] params,
                                         @NotNull Class<?> responseType) {
        return add(id, method, params).returnType(id, responseType);
    }

    /**
     * Adds a new request with a return type
     *
     * @param id           request id as a long value
     * @param method       request method
     * @param params       request params as a map of parameter names to values
     * @param responseType expected response type
     * @return the current builder
     */
    @NotNull
    public BatchRequestBuilder<K, V> add(long id, @NotNull String method, @NotNull Map<String, ?> params,
                                         @NotNull Class<?> responseType) {
        return add(id, method, params).returnType(id, responseType);
    }

    /**
     * Adds a new request with a return type
     *
     * @param id           request id as an int value
     * @param method       request method
     * @param params       request params as a map of parameter names to values
     * @param responseType expected response type
     * @return the current builder
     */
    @NotNull
    public BatchRequestBuilder<K, V> add(int id, @NotNull String method, @NotNull Map<String, ?> params,
                                         @NotNull Class<?> responseType) {
        return add(id, method, params).returnType(id, responseType);
    }

    /**
     * Adds a new request with a return type
     *
     * @param id           request id as a text value
     * @param method       request method
     * @param params       request params as a map of parameter names to values
     * @param responseType expected response type
     * @return the current builder
     */
    @NotNull
    public BatchRequestBuilder<K, V> add(String id, @NotNull String method, @NotNull Map<String, ?> params,
                                         @NotNull Class<?> responseType) {
        return add(id, method, params).returnType(id, responseType);
    }

    /**
     * Adds a new request with a complex return type
     *
     * @param id            request id as a long value
     * @param method        request method
     * @param params        request params as an array
     * @param typeReference expected complex response type
     * @return the current builder
     */
    @NotNull
    public BatchRequestBuilder<K, V> add(long id, @NotNull String method, @NotNull Object[] params,
                                         @NotNull TypeReference<?> typeReference) {
        return add(id, method, params).returnType(id, typeReference);
    }

    /**
     * Adds a new request with a complex return type
     *
     * @param id            request id as an int value
     * @param method        request method
     * @param params        request params as an array
     * @param typeReference expected complex response type
     * @return the current builder
     */
    @NotNull
    public BatchRequestBuilder<K, V> add(int id, @NotNull String method, @NotNull Object[] params,
                                         @NotNull TypeReference<?> typeReference) {
        return add(id, method, params).returnType(id, typeReference);
    }

    /**
     * Adds a new request with a complex return type
     *
     * @param id            request id as a text value
     * @param method        request method
     * @param params        request params as an array
     * @param typeReference expected complex response type
     * @return the current builder
     */
    @NotNull
    public BatchRequestBuilder<K, V> add(String id, @NotNull String method, @NotNull Object[] params,
                                         @NotNull TypeReference<?> typeReference) {
        return add(id, method, params).returnType(id, typeReference);
    }

    /**
     * Adds a new request with a complex return type
     *
     * @param id            request id as a long value
     * @param method        request method
     * @param params        request params as a map of parameter names to values
     * @param typeReference expected complex response type
     * @return the current builder
     */
    @NotNull
    public BatchRequestBuilder<K, V> add(long id, @NotNull String method, @NotNull Map<String, ?> params,
                                         @NotNull TypeReference<?> typeReference) {
        return add(id, method, params).returnType(id, typeReference);
    }

    /**
     * Adds a new request with a complex return type
     *
     * @param id            request id as an int value
     * @param method        request method
     * @param params        request params as a map of parameter names to values
     * @param typeReference expected complex response type
     * @return the current builder
     */
    @NotNull
    public BatchRequestBuilder<K, V> add(int id, @NotNull String method, @NotNull Map<String, ?> params,
                                         @NotNull TypeReference<?> typeReference) {
        return add(id, method, params).returnType(id, typeReference);
    }

    /**
     * Adds a new request with a complex return type
     *
     * @param id            request id as a text value
     * @param method        request method
     * @param params        request params as a map of parameter names to values
     * @param typeReference expected complex response type
     * @return the current builder
     */
    @NotNull
    public BatchRequestBuilder<K, V> add(String id, @NotNull String method, @NotNull Map<String, ?> params,
                                         @NotNull TypeReference<?> typeReference) {
        return add(id, method, params).returnType(id, typeReference);
    }

    /**
     * Sets an expected return type for a request
     *
     * @param id           request id
     * @param responseType expected response type
     * @return a new builder
     */
    private BatchRequestBuilder<K, V> returnType(Object id, @NotNull Class<?> responseType) {
        returnTypes.put(id, TypeFactory.defaultInstance().constructType(responseType));
        return this;
    }

    /**
     * Sets an expected return type as a complex type for a request
     *
     * @param id            request id
     * @param typeReference expected response type as a complex type
     * @return a new builder
     */
    private BatchRequestBuilder<K, V> returnType(Object id, @NotNull TypeReference<?> typeReference) {
        returnTypes.put(id, mapper.getTypeFactory().constructType(typeReference.getType()));
        return this;
    }

    /**
     * Sets type of request keys.
     * The purpose of this method is providing static and runtime type safety of processing of batch responses
     *
     * @param keysClass type of keys
     * @param <NK>      type of keys
     * @return a new builder
     */
    public <NK> BatchRequestBuilder<NK, V> keysType(@NotNull Class<NK> keysClass) {
        return new BatchRequestBuilder<NK, V>(transport, mapper, requests, returnTypes, keysClass, returnType);
    }

    /**
     * Sets an expected response type of requests.
     * This method is preferred when requests have the same response type.
     *
     * @param valuesClass expected requests return type
     * @param <NV>        expected requests return type
     * @return a new builder
     */
    public <NV> BatchRequestBuilder<K, NV> returnType(@NotNull Class<NV> valuesClass) {
        return new BatchRequestBuilder<K, NV>(transport, mapper, requests, returnTypes, keysType,
                TypeFactory.defaultInstance().constructType(valuesClass));
    }

    /**
     * Sets an expected complex response type of requests.
     *
     * @param tr   expected complex requests return type
     * @param <NV> expected requests return type
     * @return a new builder
     */
    public <NV> BatchRequestBuilder<K, NV> returnType(@NotNull TypeReference<NV> tr) {
        return new BatchRequestBuilder<K, NV>(transport, mapper, requests, returnTypes, keysType,
                mapper.constructType(tr.getType()));
    }

    /**
     * Validates, executes the request and process response
     *
     * @return map of responses by request ids
     */
    @NotNull
    public Map<K, V> execute() {
        validateRequest();
        String textResponse = executeRequest();
        return processBatchResponse(textResponse);
    }

    /**
     * Validates the request as a valid batch JSON-RPC request
     */
    private void validateRequest() {
        if (requests.isEmpty()) {
            throw new IllegalArgumentException("Requests are not set");
        }

        List<?> requestIds = requestIds();
        if (returnType == null) {
            for (Object id : requestIds) {
                if (!returnTypes.containsKey(id)) {
                    throw new IllegalArgumentException("Return type isn't specified for " +
                            "request with id='" + id + "'");
                }
            }
        } else if (!returnTypes.isEmpty()) {
            throw new IllegalArgumentException("Common and detailed configurations of return types shouldn't be mixed");
        }

        for (Object id : requestIds) {
            checkIdType(id);
        }
    }

    /**
     * Executes the request through the transport
     *
     * @return backend response as a string
     */
    @NotNull
    private String executeRequest() {
        try {
            return transport.pass( empty(), mapper.writeValueAsString(requests));
        } catch (IOException e) {
            throw new IllegalStateException("I/O error during a request processing", e);
        }
    }

    /**
     * Processes JSON-RPC batch response
     *
     * @param textResponse response as a string
     * @return map of responses (Java objects) by request ids
     */
    @NotNull
    @SuppressWarnings("unchecked")
    private Map<K, V> processBatchResponse(@NotNull String textResponse) {
        Map<Object, Object> successes = new HashMap<Object, Object>();
        Map<Object, ErrorMessage> errors = new HashMap<Object, ErrorMessage>();
        List<?> requestIds = requestIds();

        try {
            JsonNode jsonResponses = mapper.readTree(textResponse);
            // If it's an empty response
            if (jsonResponses.isTextual() && jsonResponses.asText().isEmpty() && requestIds.isEmpty()) {
                return new HashMap<K, V>();
            }
            // Not an array
            if (jsonResponses.getNodeType() != JsonNodeType.ARRAY) {
                throw new IllegalStateException("Expected array but was " + jsonResponses.getNodeType());
            }

            for (JsonNode responseNode : (ArrayNode) jsonResponses) {
                processSingleResponse(responseNode, requestIds, successes, errors);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Unable parse a JSON response: " + textResponse, e);
        }
        if (!errors.isEmpty()) {
            throw new JsonRpcBatchException("Errors happened during batch request processing", successes, errors);
        }
        return (Map<K, V>) successes;
    }

    private void processSingleResponse(@NotNull JsonNode responseNode, @NotNull List<?> requestIds,
                                       @NotNull Map<Object, Object> successes,
                                       @NotNull Map<Object, ErrorMessage> errors)
            throws JsonProcessingException {
        checkVersion(responseNode, responseNode.get(JSONRPC));

        JsonNode result = responseNode.get(RESULT);
        JsonNode error = responseNode.get(ERROR);
        if (error == null && result == null) {
            throw new IllegalStateException("Neither result or error is set in response: " + responseNode);
        }

        // Check id and convert it to long if necessary
        Object idValue = nodeValue(responseNode.get(ID));
        if (keysType == Long.class && idValue.getClass() == Integer.class) {
            idValue = ((Integer) idValue).longValue();
        }

        if (!requestIds.contains(idValue)) {
            throw new IllegalStateException("Unspecified id: '" + idValue + "' in response");
        }

        if (result != null) {
            JavaType actualReturnType = returnType != null ? returnType : returnTypes.get(idValue);
            successes.put(idValue, mapper.convertValue(result, actualReturnType));
        } else {
            // Process as an error
            errors.put(idValue, mapper.treeToValue(error, ErrorMessage.class));
        }
    }

    private void checkVersion(JsonNode responseNode, JsonNode version) {
        if (version == null) {
            throw new IllegalStateException("Not a JSON-RPC response: " + responseNode);
        }
        if (!version.asText().equals(VERSION_2_0)) {
            throw new IllegalStateException("Bad protocol version in a response: " + responseNode);
        }
    }

    private void checkIdType(@NotNull Object id) {
        if (keysType != null && !keysType.equals(id.getClass())) {
            throw new IllegalArgumentException("Id: '" + id + "' has wrong type: '" + id.getClass().getSimpleName() +
                    "'. Should be: '" + keysType.getSimpleName() + "'");
        }
    }

    @NotNull
    private List<?> requestIds() {
        List<Object> ids = new ArrayList<Object>(requests.size());
        for (ObjectNode request : requests) {
            JsonNode id = request.get(ID);
            if (id != null) {
                ids.add(nodeValue(id));
            }
        }
        return ids;
    }

    // Visible for tests
    @NotNull
    List<ObjectNode> getRequests() {
        return requests;
    }

    @NotNull
    private static Object nodeValue(@NotNull JsonNode id) {
        if (id.isLong()) {
            return id.longValue();
        } else if (id.isInt()) {
            return id.intValue();
        } else if (id.isTextual()) {
            return id.textValue();
        }
        throw new IllegalArgumentException("Wrong id=" + id);
    }

}
