package com.github.arteam.dropwizard.json.rpc.protocol.controller;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ContainerNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import com.github.arteam.dropwizard.json.rpc.protocol.domain.*;
import com.google.common.base.Defaults;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

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
    private static final String VERSION = "2.0";

    @NotNull
    private ObjectMapper mapper;

    public JsonRpcController() {
        mapper = new ObjectMapper();
    }

    public JsonRpcController(@NotNull ObjectMapper mapper) {
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
            return toJson(typedHandle(request, service));
        } catch (Exception e) {
            log.error("Internal error", e);
            return toJson(new ErrorResponse(request.getId(), INTERNAL_ERROR));
        }
    }

    private Response typedHandle(Request request, Object service) throws Exception {
        // Validate params are set
        String requestMethod = request.getMethod();
        ContainerNode requestParams = request.getParams();
        String jsonrpc = request.getJsonrpc();
        ValueNode id = request.getId();
        if (jsonrpc == null || requestMethod == null || requestParams == null) {
            log.error("Not a JSON-RPC request");
            return new ErrorResponse(id, INVALID_REQUEST);
        }

        if (!jsonrpc.equals(VERSION)) {
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

        Object[] methodParams;
        try {
            methodParams = convertToMethodParams(requestParams, method);
        } catch (IllegalArgumentException e) {
            log.error("Bad params: " + requestParams + " of a method '" + method.getName() + "'", e);
            return new ErrorResponse(id, INVALID_PARAMS);
        }

        Object result = method.invoke(service, methodParams);
        return new SuccessResponse(id, result);
    }

    /**
     * Convert JSON params to method params
     */
    @NotNull
    private Object[] convertToMethodParams(@NotNull ContainerNode<?> params,
                                           @NotNull Method method) {
        Annotation[][] allParametersAnnotations = method.getParameterAnnotations();
        int amountMethodParams = allParametersAnnotations.length;
        if (params.size() > amountMethodParams) {
            throw new IllegalArgumentException("Wrong amount arguments: " + params.size() +
                    " for a method '" + method.getName() + "'. Actual amount: " + amountMethodParams);
        }

        Object[] methodParams = new Object[amountMethodParams];
        Class<?>[] parameterTypes = method.getParameterTypes();
        for (int i = 0; i < amountMethodParams; i++) {
            Annotation[] parameterAnnotations = allParametersAnnotations[i];

            JsonRpcParam jsonRpcParam = Reflections.getAnnotation(parameterAnnotations, JsonRpcParam.class);
            if (jsonRpcParam == null) {
                throw new IllegalArgumentException("Annotation @JsonRpcParam is not set for the " + i +
                        " parameter of a method '" + method.getName() + "'");
            }

            Class<?> parameterType = parameterTypes[i];
            JsonNode jsonNode = params.isObject() ? params.get(jsonRpcParam.value()) : params.get(i);
            if (jsonNode == null) {
                if (Reflections.getAnnotation(parameterAnnotations, Optional.class) != null) {
                    // If primitive is optional
                    methodParams[i] = Defaults.defaultValue(parameterType);
                    continue;
                } else {
                    throw new IllegalArgumentException("Mandatory parameter '" + jsonRpcParam.value() +
                            "' of a method '" + method.getName() + "' is not set");
                }
            }

            try {
                methodParams[i] = mapper.treeToValue(jsonNode, parameterType);
            } catch (JsonProcessingException e) {
                throw new IllegalArgumentException("Bad param: " + jsonNode + ". " +
                        "Should have been type '" + parameterType + "'", e);
            }
        }
        return methodParams;
    }

    private String toJson(Object value) {
        try {
            return mapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            log.error("Unable write json", e);
            throw new IllegalStateException(e);
        }
    }
}
