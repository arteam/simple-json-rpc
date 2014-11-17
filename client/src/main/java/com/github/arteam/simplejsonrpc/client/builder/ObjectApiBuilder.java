package com.github.arteam.simplejsonrpc.client.builder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.*;
import com.github.arteam.simplejsonrpc.client.ParamsType;
import com.github.arteam.simplejsonrpc.client.Transport;
import com.github.arteam.simplejsonrpc.client.exception.JsonRpcException;
import com.github.arteam.simplejsonrpc.client.generator.IdGenerator;
import com.github.arteam.simplejsonrpc.client.metadata.ClassMetadata;
import com.github.arteam.simplejsonrpc.client.metadata.MethodMetadata;
import com.github.arteam.simplejsonrpc.client.metadata.ParameterMetadata;
import com.github.arteam.simplejsonrpc.core.domain.ErrorMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Date: 24.08.14
 * Time: 17:33
 *
 * @author Artem Prigoda
 */
public class ObjectApiBuilder extends AbstractBuilder implements InvocationHandler {

    private static final String RESULT = "result";
    private static final String ERROR = "error";

    @Nullable
    private ParamsType userParamsType;

    @Nullable
    private IdGenerator userIdGenerator;

    @NotNull
    private ClassMetadata classMetadata;

    public ObjectApiBuilder(@NotNull Class<?> clazz, @NotNull Transport transport, @NotNull ObjectMapper mapper,
                            @Nullable ParamsType userParamsType, @Nullable IdGenerator userIdGenerator) {
        super(transport, mapper);
        // Check that it's a service
        ClassMetadata classMetadata = Reflections.getClassMetadata(clazz);
        this.classMetadata = classMetadata;
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
        JsonNode params = getParams(methodMetadata, args, getParamsType(classMetadata, methodMetadata));
        IdGenerator<?> idGenerator = userIdGenerator != null ? userIdGenerator : classMetadata.getIdGenerator();

        //  Construct a request
        ValueNode id = new POJONode(idGenerator.generate());
        String textResponse = execute(request(id, methodName, params));

        // Parse a response
        JsonNode responseNode = mapper.readTree(textResponse);
        JsonNode result = responseNode.get(RESULT);
        JsonNode error = responseNode.get(ERROR);
        if (result != null) {
            JavaType returnType = mapper.getTypeFactory().constructType(method.getGenericReturnType());
            return mapper.convertValue(result, returnType);
        } else {
            ErrorMessage errorMessage = mapper.treeToValue(error, ErrorMessage.class);
            throw new JsonRpcException(errorMessage);
        }
    }

    /**
     * Get request params in JSON representation (map or array)
     */
    @NotNull
    private JsonNode getParams(@NotNull MethodMetadata method, @NotNull Object[] args,
                               @NotNull ParamsType paramsType) {
        ObjectNode paramsAsMap = mapper.createObjectNode();
        ArrayNode paramsAsArray = mapper.createArrayNode();
        for (String paramName : method.getParams().keySet()) {
            ParameterMetadata parameterMetadata = method.getParams().get(paramName);
            int index = parameterMetadata.getIndex();
            JsonNode jsonArg = mapper.valueToTree(args[index]);
            if (jsonArg == null || jsonArg == NullNode.instance) {
                if (parameterMetadata.isOptional()) {
                    if (paramsType == ParamsType.ARRAY) {
                        paramsAsArray.add(NullNode.instance);
                    }
                } else {
                    throw new IllegalArgumentException("Parameter '" + paramName +
                            "' of method '" + method.getName() + "' is mandatory and can't be null");
                }
            } else {
                if (paramsType == ParamsType.MAP) {
                    paramsAsMap.set(paramName, jsonArg);
                } else if (paramsType == ParamsType.ARRAY) {
                    paramsAsArray.insert(index, jsonArg);
                }
            }
        }
        return paramsType == ParamsType.MAP ? paramsAsMap : paramsAsArray;
    }

    @NotNull
    private String execute(@NotNull ObjectNode request) {
        try {
            return transport.pass(mapper.writeValueAsString(request));
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Unable convert " + request + " to JSON", e);
        } catch (IOException e) {
            throw new IllegalStateException("I/O error during request processing", e);
        }
    }

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
