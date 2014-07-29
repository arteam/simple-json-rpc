package com.github.arteam.dropwizard.json.rpc.protocol.controller;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ContainerNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import com.github.arteam.dropwizard.json.rpc.protocol.domain.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Date: 07.06.14
 * Time: 12:06
 *
 * @author Artem Prigoda
 */
public class JsonRpcController {

    private static final ErrorMessage PARSE_ERROR = new ErrorMessage(-32700, "Parse error");
    private static final ErrorMessage METHOD_NOT_FOUND = new ErrorMessage(-32601, "Method not found");
    private static final ErrorMessage INVALID_REQUEST = new ErrorMessage(-32600, "Invalid Request");
    private static final ErrorMessage INVALID_PARAMS = new ErrorMessage(-32602, "Invalid params");
    private static final ErrorMessage INTERNAL_ERROR = new ErrorMessage(-32603, "Internal error");
    private static final Logger log = LoggerFactory.getLogger(JsonRpcController.class);

    private ObjectMapper mapper = new ObjectMapper();

    public JsonRpcController() {
    }

    public JsonRpcController(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public String handle(String textRequest, Object service) {
        Request request;
        try {
            request = mapper.readValue(textRequest, Request.class);
        } catch (JsonParseException e) {
            log.error("Bad json request", e);
            return toJson(new ErrorResponse(PARSE_ERROR));
        } catch (IOException e) {
            log.error("Invalid json request", e);
            return toJson(new ErrorResponse(INVALID_REQUEST));
        }

        try {
            return toJson(handle0(request, service));
        } catch (Exception e) {
            log.error("Internal error", e);
            return toJson(new ErrorResponse(request.getId(), INTERNAL_ERROR));
        }
    }

    private Response handle0(Request request, Object service) throws Exception {
        String requestMethod = request.getMethod();
        ContainerNode requestParams = request.getParams();
        String jsonrpc = request.getJsonrpc();
        ValueNode id = request.getId();

        if (jsonrpc == null || requestMethod == null || requestParams == null) {
            log.error("Not a JSON-RPC request");
            return new ErrorResponse(id, INVALID_REQUEST);
        }

        if (!jsonrpc.equals("2.0")) {
            log.error("Not a JSON_RPC 2.0 request");
            return new ErrorResponse(id, INVALID_REQUEST);
        }

        // If it's a notification, then we can send response early
        // TODO check notification
        boolean isNotification = id == null;
        Method method = Reflections.findMethod(service.getClass(), requestMethod);
        if (method == null) {
            log.error("Unable find a method: '" + requestMethod + "' in a " + service.getClass());
            return new ErrorResponse(id, METHOD_NOT_FOUND);
        }

        Object[] convertedMethodParams;
        try {
            JsonNode[] methodParams = Reflections.getMethodParams(method, requestParams);
            convertedMethodParams = convertFromJson(methodParams, method.getParameterTypes());
        } catch (IllegalArgumentException e) {
            log.error("Bad params of " + request, e);
            return new ErrorResponse(id, INVALID_PARAMS);
        }

        Object result = method.invoke(service, convertedMethodParams);
        return new SuccessResponse(id, result);
    }


    /**
     * Check that parameters are set and have right type
     */
    @NotNull
    @SuppressWarnings("unchecked")
    private Object[] convertFromJson(@NotNull JsonNode[] jsonNodes, @NotNull Class<?>[] parameterTypes) {
        if (jsonNodes.length != parameterTypes.length) {
            throw new IllegalArgumentException("Invalid amount of arguments of " + Arrays.toString(jsonNodes));
        }

        Object[] methodParams = new Object[jsonNodes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> parameterType = parameterTypes[i];
            JsonNode jsonNode = jsonNodes[i];
            try {
                methodParams[i] = mapper.treeToValue(jsonNode, parameterType);
            } catch (Exception e) {
                throw new IllegalArgumentException("Bad param: " + jsonNode + ". " +
                        "Should have been type '" + parameterType + "'", e);
            }
        }
        return methodParams;
    }

    private String toJson(Object value) {
        try {
            return mapper.writeValueAsString(value);
        } catch (JsonProcessingException ignore) {
            throw new AssertionError(ignore);
        }
    }
}
