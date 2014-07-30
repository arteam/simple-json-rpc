package com.github.arteam.dropwizard.json.rpc.protocol.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ContainerNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
public class JsonRpcServer {

    private static final ErrorMessage PARSE_ERROR = new ErrorMessage(-32700, "Parse error");
    private static final ErrorMessage METHOD_NOT_FOUND = new ErrorMessage(-32601, "Method not found");
    private static final ErrorMessage INVALID_REQUEST = new ErrorMessage(-32600, "Invalid Request");
    private static final ErrorMessage INVALID_PARAMS = new ErrorMessage(-32602, "Invalid params");
    private static final ErrorMessage INTERNAL_ERROR = new ErrorMessage(-32603, "Internal error");

    private static final Logger log = LoggerFactory.getLogger(JsonRpcServer.class);
    private static final String VERSION = "2.0";

    @NotNull
    private ObjectMapper mapper;

    public JsonRpcServer() {
        mapper = new ObjectMapper();
    }

    public JsonRpcServer(@NotNull ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public String handle(String textRequest, Object service) {
        JsonNode rootRequest;
        try {
            rootRequest = mapper.readTree(textRequest);
        } catch (IOException e) {
            log.error("Bad json request", e);
            return toJson(new ErrorResponse(PARSE_ERROR));
        }
        if (rootRequest.isObject()) {
            Response response = convertRequestAndHandle(rootRequest, service);
            return isNotification(rootRequest, response) ? "" : toJson(response);
        } else if (rootRequest.isArray()) {
            ArrayNode responses = mapper.createArrayNode();
            for (JsonNode request : (ArrayNode) rootRequest) {
                Response response = convertRequestAndHandle(request, service);
                if (!isNotification(request, response)) {
                    responses.add(mapper.convertValue(response, ObjectNode.class));
                }
            }
            return toJson(responses);
        }
        log.error("Json primitive is not valid request");
        return toJson(new ErrorResponse(INVALID_REQUEST));
    }

    private boolean isNotification(JsonNode requestNode, Response response) {
        if (requestNode.get("id") == null) {
            if (response instanceof SuccessResponse) {
                return true;
            } else if (response instanceof ErrorResponse) {
                int errorCode = ((ErrorResponse) response).getError().getCode();
                if (errorCode != PARSE_ERROR.getCode() && errorCode != INVALID_REQUEST.getCode()) {
                    return true;
                }
            }
        }
        return false;
    }

    private Response convertRequestAndHandle(JsonNode requestNode, Object service) {
        Request request;
        try {
            request = mapper.convertValue(requestNode, Request.class);
        } catch (Exception e) {
            log.error("Invalid json request", e);
            return new ErrorResponse(INVALID_REQUEST);
        }

        try {
            return innerHandle(request, service);
        } catch (Exception e) {
            log.error("Internal error", e);
            return new ErrorResponse(request.getId(), INTERNAL_ERROR);
        }
    }

    private Response innerHandle(Request request, Object service) throws Exception {
        String requestMethod = request.getMethod();
        String jsonrpc = request.getJsonrpc();
        ValueNode id = request.getId();
        if (jsonrpc == null || requestMethod == null) {
            log.error("Not a JSON-RPC request");
            return new ErrorResponse(id, INVALID_REQUEST);
        }

        if (!jsonrpc.equals(VERSION)) {
            log.error("Not a JSON_RPC 2.0 request");
            return new ErrorResponse(id, INVALID_REQUEST);
        }

        Method method = Reflections.findMethod(service.getClass(), requestMethod);
        if (method == null) {
            log.error("Unable find a method: '" + requestMethod + "' in a " + service.getClass());
            return new ErrorResponse(id, METHOD_NOT_FOUND);
        }

        Object[] methodParams;
        ContainerNode requestParams = request.getParams();
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
        int methodParamsSize = allParametersAnnotations.length;
        int jsonParamsSize = params.size();
        if (jsonParamsSize > methodParamsSize) {
            throw new IllegalArgumentException("Wrong amount arguments: " + jsonParamsSize +
                    " for a method '" + method.getName() + "'. Actual amount: " + methodParamsSize);
        }

        Object[] methodParams = new Object[methodParamsSize];
        Class<?>[] parameterTypes = method.getParameterTypes();
        int processed = 0;
        for (int i = 0; i < methodParamsSize; i++) {
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
                processed++;
            } catch (JsonProcessingException e) {
                throw new IllegalArgumentException("Wrong type of " + i + " param: " + jsonNode + ". " +
                        "Should have type '" + parameterType + "'", e);
            }
        }
        if (processed < jsonParamsSize) {
            throw new IllegalArgumentException("Some unspecified parameters in " + params +
                    " are passed to a method '" + method.getName() + "'");
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
