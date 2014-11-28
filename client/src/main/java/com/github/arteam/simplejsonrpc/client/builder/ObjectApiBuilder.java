package com.github.arteam.simplejsonrpc.client.builder;

import com.github.arteam.simplejsonrpc.client.ParamsType;
import com.github.arteam.simplejsonrpc.client.Transport;
import com.github.arteam.simplejsonrpc.client.exception.JsonRpcException;
import com.github.arteam.simplejsonrpc.client.generator.IdGenerator;
import com.github.arteam.simplejsonrpc.client.metadata.ClassMetadata;
import com.github.arteam.simplejsonrpc.client.metadata.MethodMetadata;
import com.github.arteam.simplejsonrpc.client.metadata.ParameterMetadata;
import com.github.arteam.simplejsonrpc.core.domain.ErrorMessage;
import com.google.gson.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Date: 24.08.14
 * Time: 17:33
 * Proxy for accessing a remote JSON-RPC service trough an interface.
 *
 * @author Artem Prigoda
 */
public class ObjectApiBuilder extends AbstractBuilder implements InvocationHandler {

    @Nullable
    private ParamsType userParamsType;

    @Nullable
    private IdGenerator userIdGenerator;

    @NotNull
    private ClassMetadata classMetadata;

    /**
     * Crate a new proxy for an interface
     *
     * @param clazz           service interface
     * @param transport       transport abstraction
     * @param mapper          json mapper
     * @param userParamsType  custom type of request params
     * @param userIdGenerator custom id generator
     */
    public ObjectApiBuilder(@NotNull Class<?> clazz, @NotNull Transport transport, @NotNull Gson mapper,
                            @Nullable ParamsType userParamsType, @Nullable IdGenerator userIdGenerator) {
        super(transport, mapper);
        this.classMetadata = Reflections.getClassMetadata(clazz);
        this.userParamsType = userParamsType;
        this.userIdGenerator = userIdGenerator;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // Check that it's a JSON-RPC method
        MethodMetadata methodMetadata = classMetadata.getMethods().get(method);
        if (methodMetadata == null) {
            throw new IllegalStateException("Method '" + method.getName() + "' is not JSON-RPC available");
        }

        // Get method name (annotation or the actual name), params and id generator
        String methodName = methodMetadata.getName();
        JsonElement params = getParams(methodMetadata, args, getParamsType(classMetadata, methodMetadata));
        IdGenerator<?> idGenerator = userIdGenerator != null ? userIdGenerator : classMetadata.getIdGenerator();

        //  Construct a request
        JsonPrimitive id = createJsonPrimitive(idGenerator.generate());
        String textResponse = execute(request(id, methodName, params));

        // Parse a response
        JsonObject responseNode = mapper.fromJson(textResponse, JsonObject.class);
        JsonElement result = responseNode.get(RESULT);
        JsonElement error = responseNode.get(ERROR);
        if (result != null) {
            return mapper.fromJson(result, method.getGenericReturnType());
        } else {
            ErrorMessage errorMessage = mapper.fromJson(error, ErrorMessage.class);
            throw new JsonRpcException(errorMessage);
        }
    }

    @NotNull
    private JsonPrimitive createJsonPrimitive(@NotNull Object obj) {
        if (obj instanceof Number) {
            return new JsonPrimitive((Number) obj);
        } else if (obj instanceof String) {
            return new JsonPrimitive((String) obj);
        } else {
            throw new IllegalArgumentException(obj + " isn't number or string");
        }
    }

    /**
     * Get request params in a JSON representation (map or array)
     */
    @NotNull
    private JsonElement getParams(@NotNull MethodMetadata method, @NotNull Object[] args,
                                  @NotNull ParamsType paramsType) {
        JsonObject paramsAsMap = new JsonObject();
        JsonArray paramsAsArray = new JsonArray();
        for (String paramName : method.getParams().keySet()) {
            ParameterMetadata parameterMetadata = method.getParams().get(paramName);
            int index = parameterMetadata.getIndex();
            JsonElement jsonArg = mapper.toJsonTree(args[index], parameterMetadata.getType());
            if (jsonArg == null || jsonArg.isJsonNull()) {
                if (parameterMetadata.isOptional()) {
                    if (paramsType == ParamsType.ARRAY) {
                        paramsAsArray.add(JsonNull.INSTANCE);
                    }
                } else {
                    throw new IllegalArgumentException("Parameter '" + paramName +
                            "' of method '" + method.getName() + "' is mandatory and can't be null");
                }
            } else {
                if (paramsType == ParamsType.MAP) {
                    paramsAsMap.add(paramName, jsonArg);
                } else if (paramsType == ParamsType.ARRAY) {
                    // We preserve order during initialization
                    paramsAsArray.add(jsonArg);
                }
            }
        }
        return paramsType == ParamsType.MAP ? paramsAsMap : paramsAsArray;
    }

    /**
     * Execute a request on a remote service and return a textual representation of a response
     *
     * @param request json representation of a request
     * @return service response as a string
     */
    @NotNull
    private String execute(@NotNull JsonObject request) {
        try {
            return transport.pass(mapper.toJson(request));
        } catch (Exception e) {
            throw new IllegalStateException("I/O error during request processing", e);
        }
    }

    /**
     * Get style of params for a request.
     * It could be either on a method, class or user level. MAP is a fallback choice as default.
     *
     * @param classMetadata  metadata of a service interface
     * @param methodMetadata metadata of a method
     * @return type of params
     */
    @NotNull
    private ParamsType getParamsType(@NotNull ClassMetadata classMetadata, @NotNull MethodMetadata methodMetadata) {
        if (userParamsType != null) {
            return userParamsType;
        } else if (methodMetadata.getParamsType() != null) {
            return methodMetadata.getParamsType();
        } else if (classMetadata.getParamsType() != null) {
            return classMetadata.getParamsType();
        }
        return ParamsType.MAP;
    }

}
