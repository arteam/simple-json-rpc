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
import com.github.arteam.simplejsonrpc.core.domain.ErrorMessage;
import com.github.arteam.simplejsonrpc.core.domain.ErrorResponse;
import com.github.arteam.simplejsonrpc.core.domain.Request;
import com.github.arteam.simplejsonrpc.core.domain.Response;
import com.github.arteam.simplejsonrpc.core.domain.SuccessResponse;
import com.github.arteam.simplejsonrpc.server.metadata.ClassMetadata;
import com.github.arteam.simplejsonrpc.server.metadata.ErrorDataResolver;
import com.github.arteam.simplejsonrpc.server.metadata.MethodMetadata;
import com.github.arteam.simplejsonrpc.server.metadata.ParameterMetadata;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Date: 07.06.14
 * Time: 12:06
 * <p>Main class for processing JSON-RPC 2.0 requests from the server side.</p>
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

    private final ObjectMapper mapper;

    /**
     * Cache of classes metadata
     */
    private final ConcurrentMap<Class<?>, ClassMetadata> classesMetadata = new ConcurrentHashMap<>();
    /**
     * Cache of classes metadata
     */
    private final ConcurrentMap<Class<? extends Throwable>, ErrorDataResolver> dataResolvers = new ConcurrentHashMap<>();

    /**
     * Init JSON-RPC server
     *
     * @param mapper used-defined JSON mapper
     */
    public JsonRpcServer(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * Init JSON-RPC server with default parameters
     */
    public JsonRpcServer() {
        this(new ObjectMapper());
    }

    /**
     * Handles a JSON-RPC request(single or batch),
     * delegates processing to the service, and returns a JSON-RPC response.
     *
     * @param textRequest text representation of a JSON-RPC request
     * @param service     actual service for the request processing
     * @return text representation of a JSON-RPC response
     */
    public String handle(String textRequest, Object service) {
        return handle(service, () -> mapper.readTree(textRequest), this::toJson, () -> "");
    }

    public byte[] handle(byte[] byteRequest, Object service) {
        return handle(service, () -> mapper.readTree(byteRequest), this::toJsonByteArray, () -> new byte[]{});
    }

    public OutputStream handle(InputStream requestInputStream, OutputStream responseOutputStream,
                               Object service) {
        return handle(service, () -> mapper.readTree(requestInputStream),
                v -> toJsonOutputStream(v, responseOutputStream), ByteArrayOutputStream::new);
    }

    private <T> T handle(Object service, JsonNodeSupplier rootRequestSupplier, Function<Object, T> jsonConverter,
                         Supplier<T> emptyResponse) {
        JsonNode rootRequest;
        try {
            rootRequest = rootRequestSupplier.get();
            if (log.isDebugEnabled()) {
                log.debug("Request : {}", mapper.writeValueAsString(rootRequest));
            }
        } catch (IOException e) {
            log.error("Bad json request", e);
            return jsonConverter.apply(ErrorResponse.of(PARSE_ERROR));
        }

        // Check if a single request or a batch
        if (rootRequest.isObject()) {
            Response response = handleWrapper(rootRequest, service);
            return isNotification(rootRequest, response) ? emptyResponse.get() : jsonConverter.apply(response);
        } else if (rootRequest.isArray() && rootRequest.size() > 0) {
            ArrayNode responses = mapper.createArrayNode();
            for (JsonNode request : rootRequest) {
                Response response = handleWrapper(request, service);
                if (!isNotification(request, response)) {
                    responses.add(mapper.convertValue(response, ObjectNode.class));
                }
            }

            return responses.size() > 0 ? jsonConverter.apply(responses) : emptyResponse.get();
        }

        log.error("Invalid JSON-RPC request: " + rootRequest);
        return jsonConverter.apply(ErrorResponse.of(INVALID_REQUEST));
    }

    /**
     * Check if request is a "notification request" according to the standard.
     *
     * @param requestNode a request in a JSON tree format
     * @param response    a response in a Java object format
     * @return {@code true} if a request is a "notification request"
     */
    private boolean isNotification(JsonNode requestNode, Response response) {
        // Notification request doesn't have "id" field
        if (requestNode.get("id") == null) {
            if (response instanceof SuccessResponse) {
                return true;
            } else if (response instanceof ErrorResponse) {
                // Notification request should be a valid JSON-RPC request.
                // So if we get "Parse error" or "Invalid request"
                // we can't consider the request as a notification
                ErrorResponse errorResponse = (ErrorResponse)response;
                int errorCode = errorResponse.error().getCode();
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
    private Response handleWrapper(JsonNode requestNode, Object service) {
        Request request;
        try {
            request = mapper.convertValue(requestNode, Request.class);
        } catch (Exception e) {
            log.error("Invalid JSON-RPC request: " + requestNode, e);
            return ErrorResponse.of(INVALID_REQUEST);
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
     * Otherwise, "Internal error" message will be returned.
     *
     * @param request JSON-RPC request as a Java object
     * @param e       invocation exception
     * @return JSON-RPC error response
     */
    private ErrorResponse handleError(Request request, Exception e) {
        Throwable rootCause = getRootCause(e);
        Annotation[] annotations = rootCause.getClass().getAnnotations();
        JsonRpcError jsonRpcErrorAnnotation =
                Reflections.getAnnotation(annotations, JsonRpcError.class);
        if (jsonRpcErrorAnnotation == null) {
            return ErrorResponse.of(request.id(), INTERNAL_ERROR);
        }
        int code = jsonRpcErrorAnnotation.code();
        String message = jsonRpcErrorAnnotation.message() == null || jsonRpcErrorAnnotation.message().isEmpty() ?
                rootCause.getMessage() : jsonRpcErrorAnnotation.message();
        if (message == null || message.isEmpty()) {
            log.warn("Error message should not be empty");
            return ErrorResponse.of(request.id(), INTERNAL_ERROR);
        }
        JsonNode data;
        try {
            data = dataResolvers.computeIfAbsent(rootCause.getClass(), Reflections::buildErrorDataResolver)
                    .resolveData(rootCause)
                    .map((Function<Object, JsonNode>) mapper::valueToTree)
                    .orElse(null);
        } catch (Exception e1) {
            log.error("Error while processing error data: ", e1);
            return ErrorResponse.of(request.id(), INTERNAL_ERROR);
        }
        return ErrorResponse.of(request.id(), new ErrorMessage(code, message, data));
    }

    /**
     * Performs single JSON-RPC request and return JSON-RPC response
     *
     * @param request JSON-RPC request as a Java object
     * @param service service object
     * @return JSON-RPC response as a Java object
     * @throws Exception in case of a runtime error (reflections, business logic...)
     */
    private Response handleSingle(Request request, Object service) throws Exception {
        // Check mandatory fields and correct protocol version
        String requestMethod = request.method();
        String jsonrpc = request.jsonrpc();
        ValueNode id = request.id();
        if (jsonrpc == null || requestMethod == null) {
            log.error("Not a JSON-RPC request: " + request);
            return ErrorResponse.of(id, INVALID_REQUEST);
        }

        if (!jsonrpc.equals(VERSION)) {
            log.error("Not a JSON_RPC 2.0 request: " + request);
            return ErrorResponse.of(id, INVALID_REQUEST);
        }

        JsonNode params = request.params();
        if (!params.isObject() && !params.isArray() && !params.isNull()) {
            log.error("Params of request: '" + request + "' should be an object, an array or null");
            return ErrorResponse.of(id, INVALID_REQUEST);
        }

        ClassMetadata classMetadata = classesMetadata.computeIfAbsent(service.getClass(), Reflections::getClassMetadata);
        if (!classMetadata.service()) {
            log.warn(service.getClass() + " is not available as a JSON-RPC 2.0 service");
            return ErrorResponse.of(id, METHOD_NOT_FOUND);
        }

        MethodMetadata method = classMetadata.methods().get(requestMethod);
        if (method == null) {
            log.error("Unable find a method: '" + requestMethod + "' in a " + service.getClass());
            return ErrorResponse.of(id, METHOD_NOT_FOUND);
        }

        ContainerNode<?> notNullParams = !params.isNull() ?
                (ContainerNode<?>) params : mapper.createObjectNode();
        Object[] methodParams;
        try {
            methodParams = convertToMethodParams(notNullParams, method);
        } catch (IllegalArgumentException e) {
            log.error("Bad params: " + notNullParams + " of a method '" + method.name() + "'", e);
            return ErrorResponse.of(id, INVALID_PARAMS);
        }

        Object result;
        try {
            result = method.methodHandle().bindTo(service).invokeWithArguments(methodParams);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        return new SuccessResponse(id, result, SuccessResponse.VERSION);
    }

    /**
     * Converts JSON params to java params in the appropriate order of the invoked method
     *
     * @param params json params (map or array)
     * @param method invoked method metadata
     * @return array of java objects for passing to the method
     */
    private Object[] convertToMethodParams(ContainerNode<?> params, MethodMetadata method) {
        int methodParamsSize = method.params().size();
        int jsonParamsSize = params.size();
        // Check amount arguments
        if (jsonParamsSize > methodParamsSize) {
            throw new IllegalArgumentException("Wrong amount arguments: " + jsonParamsSize +
                    " for a method '" + method.name() + "'. Actual amount: " + methodParamsSize);
        }

        Object[] methodParams = new Object[methodParamsSize];
        int processed = 0;
        for (ParameterMetadata param : method.params().values()) {
            Class<?> parameterType = param.type();
            int index = param.index();
            String name = param.name();
            JsonNode jsonNode = params.isObject() ? params.get(name) : params.get(index);
            // Handle omitted value
            if (jsonNode == null || jsonNode.isNull()) {
                if (param.optional()) {
                    methodParams[index] = getDefaultValue(parameterType);
                    if (jsonNode != null) {
                        processed++;
                    }
                    continue;
                } else {
                    throw new IllegalArgumentException("Mandatory parameter '" + name +
                            "' of a method '" + method.name() + "' is not set");
                }
            }

            // Convert JSON object to an actual Java object
            try {
                JsonParser jsonParser = mapper.treeAsTokens(jsonNode);
                JavaType javaType = mapper.getTypeFactory().constructType(param.genericType());
                methodParams[index] = mapper.readValue(jsonParser, javaType);
                processed++;
            } catch (IOException e) {
                throw new IllegalArgumentException("Wrong param: " + jsonNode + ". Expected type: '" + param, e);
            }
        }

        // Check that some unprocessed parameters were not passed
        if (processed < jsonParamsSize) {
            throw new IllegalArgumentException("Some unspecified parameters in " + params +
                    " are passed to a method '" + method.name() + "'");
        }

        return methodParams;
    }

    @Nullable
    private Object getDefaultValue(Class<?> type) {
        if (type == java.util.Optional.class) {
            // If it's Java optional then handle it as an absent value
            return java.util.Optional.empty();
        } else if (type.isPrimitive()) {
            // If parameter is a primitive set the appropriate default value
            return defaultPrimitiveValue(type);
        }
        return null;
    }

    /**
     * Utility method for converting an object to JSON that doesn't throw an unchecked exception
     *
     * @param value object
     * @return JSON representation
     */
    private String toJson(Object value) {
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

    private byte[] toJsonByteArray(Object value) {
        try {
            byte[] response = mapper.writeValueAsBytes(value);
            if (log.isDebugEnabled()) {
                log.debug("Response: {}", Arrays.toString(response));
            }
            return response;
        } catch (JsonProcessingException e) {
            log.error("Unable write json: " + value, e);
            throw new IllegalStateException(e);
        }
    }

    private OutputStream toJsonOutputStream(Object value, OutputStream outputStream) {
        try {
            mapper.writeValue(outputStream, value);
            return outputStream;
        } catch (IOException e) {
            log.error("Unable write json: " + value, e);
            throw new IllegalStateException(e);
        }
    }

    private static Throwable getRootCause(Throwable throwable) {
        // https://github.com/google/guava/blob/v31.1/guava/src/com/google/common/base/Throwables.java#L255
        Throwable slowPointer = throwable;
        boolean advanceSlowPointer = false;
        Throwable cause;
        while ((cause = throwable.getCause()) != null) {
            throwable = cause;
            if (throwable == slowPointer) {
                throw new IllegalArgumentException("Loop in causal chain detected.", throwable);
            }
            if (advanceSlowPointer) {
                slowPointer = slowPointer.getCause();
            }
            advanceSlowPointer = !advanceSlowPointer;
        }
        return throwable;
    }

    private static Object defaultPrimitiveValue(Class<?> type) {
        if (type == boolean.class) {
            return false;
        } else if (type == char.class) {
            return '\0';
        } else if (type == byte.class) {
            return (byte) 0;
        } else if (type == short.class) {
            return (short) 0;
        } else if (type == int.class) {
            return 0;
        } else if (type == long.class) {
            return 0L;
        } else if (type == float.class) {
            return 0f;
        } else if (type == double.class) {
            return 0d;
        }
        return null;
    }

    @FunctionalInterface
    private interface JsonNodeSupplier {
        JsonNode get() throws IOException;
    }
}
