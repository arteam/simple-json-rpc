package com.github.arteam.dropwizard.json.rpc.protocol.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ContainerNode;
import com.github.arteam.dropwizard.json.rpc.protocol.domain.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;

import static com.google.common.base.Strings.nullToEmpty;

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
        } catch (IOException e) {
            log.error("Bad request json", e);
            try {
                return mapper.writeValueAsString(new ErrorResponse(PARSE_ERROR));
            } catch (JsonProcessingException ignore) {
                throw new AssertionError(ignore);
            }
        }

        try {
            return mapper.writeValueAsString(handle0(request, service));
        } catch (JsonProcessingException e) {
            log.error("Unable convert response to json", e);
            try {
                return mapper.writeValueAsString(new ErrorResponse(INTERNAL_ERROR));
            } catch (JsonProcessingException ignore) {
                throw new AssertionError(ignore);
            }
        }
    }

    private Response handle0(Request request, Object service) {
        String requestMethod = request.getMethod();
        ContainerNode requestParams = request.getParams();
        if (requestMethod == null || !nullToEmpty(request.getJsonrpc()).equals("2.0") || requestParams == null) {
            log.error("Bad " + request);
            return new ErrorResponse(request.getId(), INVALID_REQUEST);
        }

        // If it's a notification, then we can send response early
        // TODO check notification
        boolean isNotification = request.getId() == null;
        Method method = Reflections.findMethod(service.getClass(), requestMethod);
        if (method == null) {
            log.error("Unable find method " + requestMethod + " of " + service.getClass());
            return new ErrorResponse(request.getId(), METHOD_NOT_FOUND);
        }

        Object[] convertedMethodParams;
        try {
            convertedMethodParams = convertParams(Reflections.getMethodParams(method, requestParams),
                    method.getParameterTypes());
        } catch (IllegalArgumentException e) {
            log.error("Bad params of " + request, e);
            return new ErrorResponse(request.getId(), INVALID_PARAMS);
        }

        try {
            Object result = method.invoke(service, convertedMethodParams);
            return new SuccessResponse(request.getId(), result);
        } catch (Exception e) {
            log.error("Internal error", e);
            return new ErrorResponse(request.getId(), INTERNAL_ERROR);
        }
    }


    /**
     * Check that parameters are set and have right type
     */
    @NotNull
    @SuppressWarnings("unchecked")
    private Object[] convertParams(@NotNull JsonNode[] jsonNodes, @NotNull Class<?>[] parameterTypes) {
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
                throw new IllegalArgumentException("Bad param: " + jsonNode + ". Should have been " + parameterType, e);
            }
        }
        return methodParams;
    }
}
