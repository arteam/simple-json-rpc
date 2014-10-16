package com.github.arteam.simplejsonrpc.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.*;
import com.fasterxml.jackson.databind.type.SimpleType;
import com.github.arteam.simplejsonrpc.core.domain.ErrorMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.*;

/**
 * Date: 10/12/14
 * Time: 6:23 PM
 *
 * @author Artem Prigoda
 */
public class BatchRequestBuilder<K, V> extends AbstractBuilder {

    @NotNull
    private final List<ObjectNode> requests;

    @NotNull
    private final Map<Object, JavaType> returnTypes;

    @Nullable
    private final Class<K> keysType;

    @Nullable
    private final JavaType valuesType;

    public BatchRequestBuilder(@NotNull Transport transport, @NotNull ObjectMapper mapper) {
        this(transport, mapper, new ArrayList<ObjectNode>(), new HashMap<Object, JavaType>(), null, null);
    }

    public BatchRequestBuilder(@NotNull Transport transport, @NotNull ObjectMapper mapper,
                               @NotNull List<ObjectNode> requests, @NotNull Map<Object, JavaType> returnTypes,
                               @Nullable Class<K> keysType, @Nullable JavaType valuesType) {
        super(transport, mapper);
        this.requests = requests;
        this.returnTypes = returnTypes;
        this.keysType = keysType;
        this.valuesType = valuesType;
    }

    public BatchRequestBuilder<K, V> add(long id, String method, Object... params) {
        requests.add(request(new LongNode(id), method, arrayParams(params)));
        return this;
    }

    public BatchRequestBuilder<K, V> add(int id, String method, Object... params) {
        requests.add(request(new IntNode(id), method, arrayParams(params)));
        return this;
    }

    public BatchRequestBuilder<K, V> add(String id, String method, Object... params) {
        requests.add(request(new TextNode(id), method, arrayParams(params)));
        return this;
    }

    public BatchRequestBuilder<K, V> add(String method, Object... params) {
        requests.add(request(NullNode.instance, method, arrayParams(params)));
        return this;
    }

    public BatchRequestBuilder<K, V> add(long id, String method, Map<String, ?> params) {
        requests.add(request(new LongNode(id), method, objectParams(params)));
        return this;
    }

    public BatchRequestBuilder<K, V> add(int id, String method, Map<String, ?> params) {
        requests.add(request(new IntNode(id), method, objectParams(params)));
        return this;
    }

    public BatchRequestBuilder<K, V> add(String id, String method, Map<String, ?> params) {
        requests.add(request(new TextNode(id), method, objectParams(params)));
        return this;
    }

    public BatchRequestBuilder<K, V> add(String method, Map<String, ?> params) {
        requests.add(request(NullNode.instance, method, objectParams(params)));
        return this;
    }

    public BatchRequestBuilder<K, V> add(long id, String method, Object[] params, Class<?> responseType) {
        return add(id, method, params).returnType(id, responseType);
    }

    public BatchRequestBuilder<K, V> add(int id, String method, Object[] params, Class<?> responseType) {
        return add(id, method, params).returnType(id, responseType);
    }

    public BatchRequestBuilder<K, V> add(String id, String method, Object[] params, Class<?> responseType) {
        return add(id, method, params).returnType(id, responseType);
    }

    public BatchRequestBuilder<K, V> add(long id, String method, Map<String, ?> params, Class<?> responseType) {
        return add(id, method, params).returnType(id, responseType);
    }

    public BatchRequestBuilder<K, V> add(int id, String method, Map<String, ?> params, Class<?> responseType) {
        return add(id, method, params).returnType(id, responseType);
    }

    public BatchRequestBuilder<K, V> add(String id, String method, Map<String, ?> params, Class<?> responseType) {
        return add(id, method, params).returnType(id, responseType);
    }

    public BatchRequestBuilder<K, V> add(int id, String method, Object[] params, TypeReference<?> typeReference) {
        return add(id, method, params).returnType(id, typeReference);
    }

    public BatchRequestBuilder<K, V> add(String id, String method, Object[] params, TypeReference<?> typeReference) {
        return add(id, method, params).returnType(id, typeReference);
    }

    public BatchRequestBuilder<K, V> add(long id, String method, Map<String, ?> params, TypeReference<?> typeReference) {
        return add(id, method, params).returnType(id, typeReference);
    }

    public BatchRequestBuilder<K, V> add(int id, String method, Map<String, ?> params, TypeReference<?> typeReference) {
        return add(id, method, params).returnType(id, typeReference);
    }

    public BatchRequestBuilder<K, V> add(String id, String method, Map<String, ?> params, TypeReference<?> typeReference) {
        return add(id, method, params).returnType(id, typeReference);
    }

    private BatchRequestBuilder<K, V> returnType(Object id, Class<?> responseType) {
        returnTypes.put(id, SimpleType.construct(responseType));
        return this;
    }

    private BatchRequestBuilder<K, V> returnType(Object id, TypeReference<?> typeReference) {
        returnTypes.put(id, mapper.getTypeFactory().constructType(typeReference.getType()));
        return this;
    }

    public <NK> BatchRequestBuilder<NK, V> keysType(Class<NK> keysClass) {
        return new BatchRequestBuilder<NK, V>(transport, mapper, requests, returnTypes, keysClass, valuesType);
    }

    public <NV> BatchRequestBuilder<K, NV> valuesType(Class<NV> valuesClass) {
        return new BatchRequestBuilder<K, NV>(transport, mapper, requests, returnTypes, keysType,
                SimpleType.construct(valuesClass));
    }

    public <NV> BatchRequestBuilder<K, NV> valuesType(TypeReference<NV> tr) {
        return new BatchRequestBuilder<K, NV>(transport, mapper, requests, returnTypes, keysType,
                mapper.constructType(tr.getType()));
    }

    @NotNull
    public Map<K, V> execute() {
        validateRequest();
        String textResponse = executeRequest();
        return processBatchResponse(textResponse);
    }

    private void validateRequest() {
        if (requests.isEmpty()) {
            throw new IllegalArgumentException("Requests are not set");
        }

        if (valuesType == null) {
            for (Object id : getRequestIds(requests)) {
                if (!returnTypes.containsKey(id)) {
                    throw new IllegalArgumentException("Return type isn't specified for " +
                            "request with id='" + id + "'");
                }
            }
        } else if (!returnTypes.isEmpty()) {
            throw new IllegalArgumentException("Common and detailed configurations of return types shouldn't be mixed");
        }

        for (Object id : getRequestIds(requests)) {
            checkIdType(id);
        }
        for (Object id : returnTypes.keySet()) {
            checkIdType(id);
        }
    }

    @NotNull
    private String executeRequest() {
        try {
            return transport.pass(mapper.writeValueAsString(requests));
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Unable convert " + requests + " to JSON", e);
        }catch (IOException e) {
            throw new IllegalStateException("I/O error during a request processing", e);
        }
    }

    @NotNull
    @SuppressWarnings("unchecked")
    private Map<K, V> processBatchResponse(@NotNull String textResponse) {
        Map<Object, Object> successes = new HashMap<Object, Object>();
        Map<Object, ErrorMessage> errors = new HashMap<Object, ErrorMessage>();
        try {
            JsonNode jsonResponses = mapper.readTree(textResponse);
            // If it's an empty response
            if (jsonResponses.isTextual() && jsonResponses.asText().isEmpty() && getRequestIds(requests).isEmpty()) {
                return new HashMap<K, V>();
            }
            // Not an array
            if (jsonResponses.getNodeType() != JsonNodeType.ARRAY) {
                throw new IllegalStateException("Expected array but was " + jsonResponses.getNodeType());
            }

            for (JsonNode responseNode : (ArrayNode) jsonResponses) {
                processSingleResponse(responseNode, successes, errors);
            }
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Unable parse a JSON response: " + textResponse, e);
        } catch (IOException e) {
            throw new IllegalStateException("I/O error during a response processing", e);
        }
        if (!errors.isEmpty()) {
            throw new BatchRequestException("Errors happened during batch request processing", successes, errors);
        }
        return (Map<K, V>) successes;
    }

    private void processSingleResponse(@NotNull JsonNode responseNode, @NotNull Map<Object, Object> successes,
                                       @NotNull Map<Object, ErrorMessage> errors) throws JsonProcessingException {
        checkVersion(responseNode, responseNode.get(JSONRPC));

        JsonNode result = responseNode.get(RESULT);
        JsonNode error = responseNode.get(ERROR);
        if (error == null && result == null) {
            throw new IllegalStateException("Neither result or error is set in a response: " + responseNode);
        }

        // Check id and convert it to long if necessary
        Object idValue = nodeValue(responseNode.get(ID));
        if (keysType == Long.class && idValue.getClass() == Integer.class) {
            idValue = ((Integer) idValue).longValue();
        }

        if (result != null) {
            // Guess a return type
            JavaType returnType;
            if (valuesType != null) {
                returnType = valuesType;
            } else {
                if ((returnType = returnTypes.get(idValue)) == null) {
                    throw new IllegalStateException("Unspecified id: '" + idValue + "' in response");
                }
            }
            successes.put(idValue, mapper.convertValue(result, returnType));
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
            throw new IllegalArgumentException("Id: '" + id + "' has wrong type: '" + id.getClass() + "'. Should be: '" + keysType + "'");
        }
    }

    @NotNull
    private static List<?> getRequestIds(@NotNull List<ObjectNode> requests) {
        List<Object> ids = new ArrayList<Object>(requests.size());
        for (ObjectNode request : requests) {
            JsonNode id = request.get(ID);
            if (id != null) {
                ids.add(nodeValue(id));
            }
        }
        return ids;
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
