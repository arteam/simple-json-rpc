package com.github.arteam.simplejsonrpc.client.builder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import com.github.arteam.simplejsonrpc.client.Transport;

import java.util.Map;

/**
 * Date: 10/12/14
 * Time: 6:48 PM
 * <p>
 * Abstract builder for JSON-RPC requests
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
    protected final Transport transport;

    /**
     * Jackson mapper for JSON processing
     */
    protected final ObjectMapper mapper;

    public AbstractBuilder(Transport transport, ObjectMapper mapper) {
        this.transport = transport;
        this.mapper = mapper;
    }

    /**
     * Builds request params as a JSON array
     *
     * @param values request params
     * @return a new JSON array
     */
    protected ArrayNode arrayParams(Object[] values) {
        ArrayNode newArrayParams = mapper.createArrayNode();
        for (Object value : values) {
            newArrayParams.add(mapper.valueToTree(value));
        }
        return newArrayParams;
    }

    /**
     * Builds request params as a JSON object
     *
     * @param params request params
     * @return a new JSON object
     */
    protected ObjectNode objectParams(Map<String, ?> params) {
        ObjectNode objectNode = mapper.createObjectNode();
        for (String key : params.keySet()) {
            objectNode.set(key, mapper.valueToTree(params.get(key)));
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
    protected ObjectNode request(ValueNode id, String method,
                                 JsonNode params) {
        if (method.isEmpty()) {
            throw new IllegalArgumentException("Method is not set");
        }
        ObjectNode requestNode = mapper.createObjectNode();
        requestNode.put(JSONRPC, VERSION_2_0);
        requestNode.put(METHOD, method);
        requestNode.set(PARAMS, params);
        if (!id.isNull()) {
            requestNode.set(ID, id);
        }
        return requestNode;
    }
}
