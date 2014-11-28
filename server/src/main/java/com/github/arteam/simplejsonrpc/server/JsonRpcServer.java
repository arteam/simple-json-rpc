package com.github.arteam.simplejsonrpc.server;

import com.github.arteam.simplejsonrpc.core.annotation.JsonRpcError;
import com.github.arteam.simplejsonrpc.core.domain.*;
import com.github.arteam.simplejsonrpc.server.metadata.ClassMetadata;
import com.github.arteam.simplejsonrpc.server.metadata.MethodMetadata;
import com.github.arteam.simplejsonrpc.server.metadata.ParameterMetadata;
import com.google.common.base.Defaults;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheBuilderSpec;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Range;
import com.google.gson.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 *
 * @author Artem Prigoda
 */
public class JsonRpcServer {

    // Error messages
    private static final ErrorMessage PARSE_ERROR = new ErrorMessage(-32700, "Parse error");
    private static final ErrorMessage METHOD_NOT_FOUND = new ErrorMessage(-32601, "Method not found");
    private static final ErrorMessage INVALID_REQUEST = new ErrorMessage(-32600, "Invalid Request");
    private static final ErrorMessage INVALID_PARAMS = new ErrorMessage(-32602, "Invalid params");
    private static final ErrorMessage INTERNAL_ERROR = new ErrorMessage(-32603, "Internal error");

    private static final int MIN_SERVER_ERROR_CODE = -32099;
    private static final int MAX_SERVER_ERROR_CODE = -32000;
    private static final Logger log = LoggerFactory.getLogger(JsonRpcServer.class);
    private static final String VERSION = "2.0";

    @NotNull
    private Gson gson;

    /**
     * Default Cache params specification
     */
    private static final CacheBuilderSpec DEFAULT_SPEC = CacheBuilderSpec.parse("expireAfterWrite=1h");

    /**
     * Cache of classes metadata
     */
    private LoadingCache<Class<?>, ClassMetadata> classesMetadata;

    /**
     * Init JSON-RPC server
     *
     * @param gson             used-defined JSON gson
     * @param cacheBuilderSpec classes metadata cache specification
     */
    public JsonRpcServer(@NotNull Gson gson, @NotNull CacheBuilderSpec cacheBuilderSpec) {
        this.gson = gson;
        classesMetadata = CacheBuilder.from(cacheBuilderSpec).build(
                new CacheLoader<Class<?>, ClassMetadata>() {
                    @Override
                    public ClassMetadata load(Class<?> clazz) throws Exception {
                        return Reflections.getClassMetadata(clazz);
                    }
                });
    }

    /**
     * Init JSON-RPC server with default parameters
     */
    public JsonRpcServer() {
        this(new Gson(), DEFAULT_SPEC);
    }

    /**
     * Factory for creating a JSON-RPC server with a specific JSON gson
     *
     * @param gson user-defined JSON gson
     * @return new JSON-RPC server
     */
    public static JsonRpcServer withGson(@NotNull Gson gson) {
        return new JsonRpcServer(gson, DEFAULT_SPEC);
    }

    /**
     * Factory for creating JSON-RPC server with a specific config of classes metadata cache.
     *
     * @param cacheSpec user-defined cache config
     * @return new JSON-RPC server
     */
    public static JsonRpcServer withCacheSpec(@NotNull CacheBuilderSpec cacheSpec) {
        return new JsonRpcServer(new GsonBuilder().serializeNulls().create(), cacheSpec);
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
        JsonElement rootRequest;
        try {
            rootRequest = gson.fromJson(textRequest, JsonElement.class);
            if (log.isDebugEnabled()) {
                log.debug("Request : {}", gson.toJson(rootRequest));
            }
        } catch (Exception e) {
            log.error("Bad json request", e);
            return toJson(new ErrorResponse(PARSE_ERROR));
        }

        // Check if a single request or a batch
        if (rootRequest.isJsonObject()) {
            Response response = handleWrapper(rootRequest, service);
            return isNotification(rootRequest.getAsJsonObject(), response) ? "" : toJson(response);
        } else if (rootRequest.isJsonArray() && rootRequest.getAsJsonArray().size() > 0) {
            JsonArray responses = new JsonArray();
            for (JsonElement request : rootRequest.getAsJsonArray()) {
                Response response = handleWrapper(request, service);
                if (!request.isJsonObject() || !isNotification(request.getAsJsonObject(), response)) {
                    responses.add(gson.toJsonTree(response));
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
    private boolean isNotification(@NotNull JsonObject requestNode, @NotNull Response response) {
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
    private Response handleWrapper(@NotNull JsonElement requestNode, @NotNull Object service) {
        Request request;
        try {
            request = gson.fromJson(requestNode, Request.class);
            if (!request.getId().isJsonNull() && !request.getId().isJsonPrimitive()) {
                throw new IllegalStateException("Id should be null or primitive");
            }
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
        if (jsonRpcErrorAnnotation != null) {
            do {
                int code = jsonRpcErrorAnnotation.code();
                String message = Strings.isNullOrEmpty(jsonRpcErrorAnnotation.message()) ?
                        rootCause.getMessage() : jsonRpcErrorAnnotation.message();
                if (code < MIN_SERVER_ERROR_CODE || code > MAX_SERVER_ERROR_CODE) {
                    log.warn("Error code=" + code + " not in a range [-32099;-32000]");
                    break;
                }
                if (Strings.isNullOrEmpty(message)) {
                    log.warn("Error message should not be empty");
                    break;
                }
                return new ErrorResponse(request.getId(), new ErrorMessage(code, message));
            } while (false);

        }
        return new ErrorResponse(request.getId(), INTERNAL_ERROR);
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
        JsonElement id = request.getId();
        if (jsonrpc == null || requestMethod == null) {
            log.error("Not a JSON-RPC request: " + request);
            return new ErrorResponse(id, INVALID_REQUEST);
        }

        if (!jsonrpc.equals(VERSION)) {
            log.error("Not a JSON_RPC 2.0 request: " + request);
            return new ErrorResponse(id, INVALID_REQUEST);
        }

        JsonElement params = request.getParams();
        if (params.isJsonPrimitive()) {
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

        JsonElement notNullParams = !params.isJsonNull() ? params : new JsonObject();
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
    private Object[] convertToMethodParams(@NotNull JsonElement params,
                                           @NotNull MethodMetadata method) {
        int methodParamsSize = method.getParams().size();
        int jsonParamsSize = params.isJsonObject() ? params.getAsJsonObject().entrySet().size() :
                params.getAsJsonArray().size();
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
            JsonElement jsonElement = params.isJsonObject() ? params.getAsJsonObject().get(name) :
                    params.getAsJsonArray().get(index);
            // Handle omitted value
            if (jsonElement == null || jsonElement.isJsonNull()) {
                if (param.isOptional()) {
                    methodParams[index] = getDefaultValue(parameterType);
                    if (jsonElement != null) {
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
                methodParams[index] = gson.fromJson(jsonElement, param.getGenericType());
                processed++;
            } catch (Exception e) {
                throw new IllegalArgumentException("Wrong param: " + jsonElement + ". Expected type: '" + param, e);
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
        if (type == Optional.class) {
            // If it's Guava optional then handle it as an absent value
            return Optional.absent();
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
            String response = gson.toJson(value);
            if (log.isDebugEnabled()) {
                log.debug("Response: {}", response);
            }
            return response;
        } catch (Exception e) {
            log.error("Unable write json: " + value, e);
            throw new IllegalStateException(e);
        }
    }
}
