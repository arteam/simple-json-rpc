package com.github.arteam.dropwizard.json.rpc.protocol.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ContainerNode;
import com.github.arteam.dropwizard.json.rpc.protocol.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
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

    public Response handle(Request request, Object service) {

        // If it's a notification, then we can send response early
        // TODO check notification
        boolean isNotification = request.getId() == null;

        String requestMethod = request.getMethod();
        ContainerNode<?> requestParams = request.getParams();

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
