package com.github.arteam.simplejsonrpc.server;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ContainerNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import com.github.arteam.simplejsonrpc.core.annotation.JsonRpcError;
import com.github.arteam.simplejsonrpc.core.domain.*;
import com.github.arteam.simplejsonrpc.server.metadata.ClassMetadata;
import com.github.arteam.simplejsonrpc.server.metadata.ErrorDataResolver;
import com.github.arteam.simplejsonrpc.server.metadata.MethodMetadata;
import com.github.arteam.simplejsonrpc.server.metadata.ParameterMetadata;
import com.google.common.base.Defaults;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheBuilderSpec;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;

/**
 * Date: 07.06.14
 * Time: 12:06
 * <p/>
 * Main class for processing JSON-RPC 2.0 requests from the server side.
 * <p/>
 * <ol>
 * <li>Converts a text JSON-RPC request to a Java JSON tree object</li>
 * <li>Checks that the request is conform with the JSON-RPC 2.0 standard</li>
 * <li>Scans metadata of a service assigned for request processing</li>
 * <li>Finds a correspondent method</li>
 * <li>Prepares the method params based on the method metadata and the incoming request params</li>
 * <li>Invokes the method</li>
 * <li>Prepares a JSON-tree response and convert it to text representation</li>
 * <li>In case of a error return appropriate returns an error message according to the standard</li>
 * </ol>
 */
public class JsonRpcServer {

    // Error messages
    private static final ErrorMessage PARSE_ERROR = new ErrorMessage(-32700, "Parse error", null);
    private static final ErrorMessage METHOD_NOT_FOUND = new ErrorMessage(-32601, "Method not found", null);
    private static final ErrorMessage INVALID_REQUEST = new ErrorMessage(-32600, "Invalid Request", null);
    private static final ErrorMessage INVALID_PARAMS = new ErrorMessage(-32602, "Invalid params", null);
    private static final ErrorMessage INTERNAL_ERROR = new ErrorMessage(-32603, "Internal error", null);

    private static final Logger log = LoggerFactory.getLogger(JsonRpcServer.class);
    private static final String VERSION = "2.0";

    @NotNull
    private ObjectMapper mapper;

    /**
     * Default Cache params specification
     */
    private static final CacheBuilderSpec DEFAULT_SPEC = CacheBuilderSpec.parse("expireAfterWrite=1h");

    /**
     * Cache of classes metadata
     */
    private LoadingCache<Class<?>, ClassMetadata> classesMetadata;
    /**
     * Cache of classes metadata
     */
    private LoadingCache<Class<? extends Throwable>, ErrorDataResolver> dataResolvers;

    /**
     * Init JSON-RPC server
     *
     * @param mapper           used-defined JSON mapper
     * @param cacheBuilderSpec classes metadata cache specification
     */
    public JsonRpcServer(@NotNull ObjectMapper mapper, @NotNull CacheBuilderSpec cacheBuilderSpec) {
        this.mapper = mapper;
        classesMetadata = CacheBuilder.from(cacheBuilderSpec).build(
            new CacheLoader<Class<?>, ClassMetadata>() {
                @Override
                public ClassMetadata load(Class<?> clazz) throws Exception {
                    return Reflections.getClassMetadata(clazz);
                }
            });
        dataResolvers = CacheBuilder.from(cacheBuilderSpec).build(
            new CacheLoader<Class<? extends Throwable>, ErrorDataResolver>() {
                @Override
                public ErrorDataResolver load(Class<? extends Throwable> clazz) throws Exception {
                    return Reflections.buildErrorDataResolver(clazz);
                }
            });
    }

    /**
     * Init JSON-RPC server with default parameters
     */
    public JsonRpcServer() {
        this(new ObjectMapper(), DEFAULT_SPEC);
    }

    /**
     * Factory for creating a JSON-RPC server with a specific JSON mapper
     *
     * @param mapper user-defined JSON mapper
     * @return new JSON-RPC server
     */
    public static JsonRpcServer withMapper(@NotNull ObjectMapper mapper) {
        return new JsonRpcServer(mapper, DEFAULT_SPEC);
    }

    /**
     * Factory for creating JSON-RPC server with a specific config of classes metadata cache.
     *
     * @param cacheSpec user-defined cache config
     * @return new JSON-RPC server
     */
    public static JsonRpcServer withCacheSpec(@NotNull CacheBuilderSpec cacheSpec) {
        return new JsonRpcServer(new ObjectMapper(), cacheSpec);
    }

    /**
     * Handles a JSON-RPC request(single or batch),
     * delegates processing to the service, and returns a JSON-RPC response.
     *
     * @param textRequest text representation of a JSON-RPC request
     * @param service     actual service for the request processing
     * @return text representation of a JSON-RPC response
     */
    @NotNull
    public String handle(@NotNull String textRequest, @NotNull Object service) {
        JsonNode rootRequest;
        try {
            rootRequest = mapper.readTree(textRequest);
            if (log.isDebugEnabled()) {
                log.debug("Request : {}", mapper.writeValueAsString(rootRequest));
            }
        } catch (IOException e) {
            log.error("Bad json request", e);
            return toJson(new ErrorResponse(PARSE_ERROR));
        }

        // Check if a single request or a batch
        if (rootRequest.isObject()) {
            Response response = handleWrapper(rootRequest, service);
            return isNotification(rootRequest, response) ? "" : toJson(response);
        } else if (rootRequest.isArray() && rootRequest.size() > 0) {
            ArrayNode responses = mapper.createArrayNode();
            for (JsonNode request : (ArrayNode) rootRequest) {
                Response response = handleWrapper(request, service);
                if (!isNotification(request, response)) {
                    responses.add(mapper.convertValue(response, ObjectNode.class));
                }
            }

            return responses.size() > 0 ? toJson(responses) : "";
        }

        log.error("Invalid JSON-RPC request: " + rootRequest);
        return toJson(new ErrorResponse(INVALID_REQUEST));
    }

    /**
     * Check if request is a "notification request" according to the standard.
     *
     * @param requestNode a request in a JSON tree format
     * @param response    a response in a Java object format
     * @return {@code true} if a request is a "notification request"
     */
    private boolean isNotification(@NotNull JsonNode requestNode, @NotNull Response response) {
        // Notification request doesn't have "id" field
        if (requestNode.get("id") == null) {
            if (response instanceof SuccessResponse) {
                return true;
            } else if (response instanceof ErrorResponse) {
                // Notification request should be a valid JSON-RPC request.
                // So if we get "Parse error" or "Invalid request"
                // we can't consider the request as a notification
                int errorCode = ((ErrorResponse) response).getError().getCode();
                if (errorCode != PARSE_ERROR.getCode() && errorCode != INVALID_REQUEST.getCode()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Wrapper around a single JSON-RPC request.
     * Checks that a request is valid JSON-RPC object and handle runtime errors in the request processing.
     *
     * @param requestNode JSON-RPC request as a JSON tree
     * @param service     service object
     * @return JSON-RPC response as a Java object
     */
    @NotNull
    private Response handleWrapper(@NotNull JsonNode requestNode, @NotNull Object service) {
        Request request;
        try {
            request = mapper.convertValue(requestNode, Request.class);
        } catch (Exception e) {
            log.error("Invalid JSON-RPC request: " + requestNode, e);
            return new ErrorResponse(INVALID_REQUEST);
        }

        try {
            return handleSingle(request, service);
        } catch (Exception e) {
            Throwable realException = e instanceof InvocationTargetException ? e.getCause() : e;
            log.error("Error while processing: " + request, realException);
            return handleError(request, e);
        }
    }

    /**
     * Handles a runtime exception. If root exception is marked with {@link JsonRpcError} annotation,
     * it will be converted to appropriate error message.
     * Otherwise "Internal error" message will be returned.
     *
     * @param request JSON-RPC request as a Java object
     * @param e       invocation exception
     * @return JSON-RPC error response
     */
    @NotNull
    private ErrorResponse handleError(@NotNull Request request, @NotNull Exception e) {
        Throwable rootCause = Throwables.getRootCause(e);
        Annotation[] annotations = rootCause.getClass().getAnnotations();
        JsonRpcError jsonRpcErrorAnnotation =
                Reflections.getAnnotation(annotations, JsonRpcError.class);
        if (jsonRpcErrorAnnotation == null) {
            return new ErrorResponse(request.getId(), INTERNAL_ERROR);
        }
        int code = jsonRpcErrorAnnotation.code();
        String message = Strings.isNullOrEmpty(jsonRpcErrorAnnotation.message()) ?
                rootCause.getMessage() : jsonRpcErrorAnnotation.message();
        if (Strings.isNullOrEmpty(message)) {
            log.warn("Error message should not be empty");
            return new ErrorResponse(request.getId(), INTERNAL_ERROR);
        }
        JsonNode data;
        try {
            data = dataResolvers.get(rootCause.getClass()).resolveData(rootCause)
                    .<JsonNode>map(mapper::valueToTree)
                    .orElse(null);
        } catch (Exception e1) {
            log.error("Error while processing error data: ", e1);
            return new ErrorResponse(request.getId(), INTERNAL_ERROR);
        }
        return new ErrorResponse(request.getId(), new ErrorMessage(code, message, data));
    }

    /**
     * Performs single JSON-RPC request and return JSON-RPC response
     *
     * @param request JSON-RPC request as a Java object
     * @param service service object
     * @return JSON-RPC response as a Java object
     * @throws Exception in case of a runtime error (reflections, business logic...)
     */
    @NotNull
    private Response handleSingle(@NotNull Request request, @NotNull Object service) throws Exception {
        // Check mandatory fields and correct protocol version
        String requestMethod = request.getMethod();
        String jsonrpc = request.getJsonrpc();
        ValueNode id = request.getId();
        if (jsonrpc == null || requestMethod == null) {
            log.error("Not a JSON-RPC request: " + request);
            return new ErrorResponse(id, INVALID_REQUEST);
        }

        if (!jsonrpc.equals(VERSION)) {
            log.error("Not a JSON_RPC 2.0 request: " + request);
            return new ErrorResponse(id, INVALID_REQUEST);
        }

        JsonNode params = request.getParams();
        if (!params.isObject() && !params.isArray() && !params.isNull()) {
            log.error("Params of request: '" + request + "' should be an object, an array or null");
            return new ErrorResponse(id, INVALID_REQUEST);
        }

        ClassMetadata classMetadata = classesMetadata.get(service.getClass());
        if (!classMetadata.isService()) {
            log.warn(service.getClass() + " is not available as a JSON-RPC 2.0 service");
            return new ErrorResponse(id, METHOD_NOT_FOUND);
        }

        MethodMetadata method = classMetadata.getMethods().get(requestMethod);
        if (method == null) {
            log.error("Unable find a method: '" + requestMethod + "' in a " + service.getClass());
            return new ErrorResponse(id, METHOD_NOT_FOUND);
        }

        ContainerNode<?> notNullParams = !params.isNull() ?
                (ContainerNode<?>) params : mapper.createObjectNode();
        Object[] methodParams;
        try {
            methodParams = convertToMethodParams(notNullParams, method);
        } catch (IllegalArgumentException e) {
            log.error("Bad params: " + notNullParams + " of a method '" + method.getName() + "'", e);
            return new ErrorResponse(id, INVALID_PARAMS);
        }

        Object result = method.getMethod().invoke(service, methodParams);
        return new SuccessResponse(id, result);
    }

    /**
     * Converts JSON params to java params in the appropriate order of the invoked method
     *
     * @param params json params (map or array)
     * @param method invoked method metadata
     * @return array of java objects for passing to the method
     */
    @NotNull
    private Object[] convertToMethodParams(@NotNull ContainerNode<?> params,
                                           @NotNull MethodMetadata method) {
        int methodParamsSize = method.getParams().size();
        int jsonParamsSize = params.size();
        // Check amount arguments
        if (jsonParamsSize > methodParamsSize) {
            throw new IllegalArgumentException("Wrong amount arguments: " + jsonParamsSize +
                    " for a method '" + method.getName() + "'. Actual amount: " + methodParamsSize);
        }

        Object[] methodParams = new Object[methodParamsSize];
        int processed = 0;
        for (ParameterMetadata param : method.getParams().values()) {
            Class<?> parameterType = param.getType();
            int index = param.getIndex();
            String name = param.getName();
            JsonNode jsonNode = params.isObject() ? params.get(name) : params.get(index);
            // Handle omitted value
            if (jsonNode == null || jsonNode.isNull()) {
                if (param.isOptional()) {
                    methodParams[index] = getDefaultValue(parameterType);
                    if (jsonNode != null) {
                        processed++;
                    }
                    continue;
                } else {
                    throw new IllegalArgumentException("Mandatory parameter '" + name +
                            "' of a method '" + method.getName() + "' is not set");
                }
            }

            // Convert JSON object to an actual Java object
            try {
                JsonParser jsonParser = mapper.treeAsTokens(jsonNode);
                JavaType javaType = mapper.getTypeFactory().constructType(param.getGenericType());
                methodParams[index] = mapper.readValue(jsonParser, javaType);
                processed++;
            } catch (IOException e) {
                throw new IllegalArgumentException("Wrong param: " + jsonNode + ". Expected type: '" + param, e);
            }
        }

        // Check that some unprocessed parameters were not passed
        if (processed < jsonParamsSize) {
            throw new IllegalArgumentException("Some unspecified parameters in " + params +
                    " are passed to a method '" + method.getName() + "'");
        }

        return methodParams;
    }

    @Nullable
    private Object getDefaultValue(@NotNull Class<?> type) {
        if (type == com.google.common.base.Optional.class) {
            // If it's Guava optional then handle it as an absent value
            return com.google.common.base.Optional.absent();
        } else if (type == java.util.Optional.class) {
            // If it's Java optional then handle it as an absent value
            return java.util.Optional.empty();
        } else if (type.isPrimitive()) {
            // If parameter is a primitive set the appropriate default value
            return Defaults.defaultValue(type);
        }
        return null;
    }

    /**
     * Utility method for converting an object to JSON that doesn't throws an unchecked exception
     *
     * @param value object
     * @return JSON representation
     */
    @NotNull
    private String toJson(@NotNull Object value) {
        try {
            String response = mapper.writeValueAsString(value);
            if (log.isDebugEnabled()) {
                log.debug("Response: {}", response);
            }
            return response;
        } catch (JsonProcessingException e) {
            log.error("Unable write json: " + value, e);
            throw new IllegalStateException(e);
        }
    }
}
