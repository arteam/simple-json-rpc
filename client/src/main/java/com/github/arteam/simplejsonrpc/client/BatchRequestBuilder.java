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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private final Map<JsonNode, JavaType> returnTypes;

    @Nullable
    private final Class<K> keysType;

    @Nullable
    private final JavaType valuesType;

    public BatchRequestBuilder(@NotNull Transport transport, @NotNull ObjectMapper mapper) {
        this(transport, mapper, new ArrayList<ObjectNode>(), new HashMap<JsonNode, JavaType>(), null, null);
    }

    public BatchRequestBuilder(@NotNull Transport transport, @NotNull ObjectMapper mapper,
                               @NotNull List<ObjectNode> requests, @NotNull Map<JsonNode, JavaType> returnTypes,
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

    public BatchRequestBuilder<K, V> returnType(long id, Class<?> responseType) {
        returnTypes.put(new LongNode(id), SimpleType.construct(responseType));
        return this;
    }

    public BatchRequestBuilder<K, V> returnType(int id, Class<?> responseType) {
        returnTypes.put(new IntNode(id), SimpleType.construct(responseType));
        return this;
    }

    public BatchRequestBuilder<K, V> returnType(String id, Class<?> responseType) {
        returnTypes.put(new TextNode(id), SimpleType.construct(responseType));
        return this;
    }

    public BatchRequestBuilder<K, V> returnType(long id, TypeReference<?> typeReference) {
        returnTypes.put(new LongNode(id), mapper.getTypeFactory().constructType(typeReference.getType()));
        return this;
    }

    public BatchRequestBuilder<K, V> returnType(int id, TypeReference<?> typeReference) {
        returnTypes.put(new IntNode(id), mapper.getTypeFactory().constructType(typeReference.getType()));
        return this;
    }

    public BatchRequestBuilder<K, V> returnType(String id, TypeReference<?> typeReference) {
        returnTypes.put(new TextNode(id), mapper.getTypeFactory().constructType(typeReference.getType()));
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

    @SuppressWarnings("unchecked")
    public Map<K, V> execute() {
        validate();

        String textResponse = executeRequest();

        Map<Object, Object> successes = new HashMap<Object, Object>();
        Map<Object, ErrorMessage> errors = new HashMap<Object, ErrorMessage>();
        try {
            ArrayNode arrayNode = (ArrayNode) mapper.readTree(textResponse);
            for (JsonNode responseNode : arrayNode) {
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
                if (error == null && result == null) {
                    throw new IllegalStateException("Neither result or error is set in a response: " + responseNode);
                }
                if (id == null) {
                    continue;
                }
                if (error != null) {
                    errors.put(nodeValue(id), mapper.treeToValue(error, ErrorMessage.class));
                    continue;
                }
                JavaType javaType = valuesType != null ? valuesType : returnTypes.get(id);
                if (javaType != null) {
                    successes.put(nodeValue(id), mapper.convertValue(result, javaType));
                } else {
                    throw new IllegalStateException("Unspecified id: '" + id + "' in response");
                }
            }
            if (!errors.isEmpty()) {
                throw new BatchRequestException("Errors happened during batch request processing", successes, errors);
            }
            return (Map<K, V>) successes;
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Unable parse a JSON response: " + textResponse, e);
        } catch (IOException e) {
            throw new IllegalStateException("I/O error during a response processing", e);
        }
    }

    private void validate() {
        if (requests.isEmpty()) {
            throw new IllegalArgumentException("Requests are not set");
        }

        if (valuesType == null) {
            if (returnTypes.isEmpty()) {
                throw new IllegalArgumentException("Return types are not set");
            }
            if (requests.size() != returnTypes.size()) {
                throw new IllegalArgumentException("Return types aren't specified for all requests");
            }
        } else if (!returnTypes.isEmpty()) {
            throw new IllegalArgumentException("Common and detailed configurations of return types shouldn't be mixed");
        }

        if (keysType != null) {
            for (JsonNode node : returnTypes.keySet()) {
                checkKeyType(node, keysType);
            }
            for (ObjectNode request : requests) {
                checkKeyType(request.get(ID), keysType);
            }
        }
    }

    private static void checkKeyType(@NotNull JsonNode node, @NotNull Class<?> keysType) {
        Object key = nodeValue(node);
        if (!keysType.equals(key.getClass())) {
            throw new IllegalArgumentException("Key: '" + key + "' has wrong type: '" + key.getClass() + "'. Should be: '" + keysType + "'");
        }
    }

    private String executeRequest() {
        String textRequest;
        String textResponse;
        try {
            textRequest = mapper.writeValueAsString(requests);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Unable convert " + requests + " to JSON", e);
        }
        try {
            textResponse = transport.pass(textRequest);
        } catch (IOException e) {
            throw new IllegalStateException("I/O error during a request processing", e);
        }
        return textResponse;
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
