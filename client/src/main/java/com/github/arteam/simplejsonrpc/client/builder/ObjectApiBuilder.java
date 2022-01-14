package com.github.arteam.simplejsonrpc.client.builder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.POJONode;
import com.fasterxml.jackson.databind.node.ValueNode;
import com.github.arteam.simplejsonrpc.client.ParamsType;
import com.github.arteam.simplejsonrpc.client.Transport;
import com.github.arteam.simplejsonrpc.client.exception.JsonRpcException;
import com.github.arteam.simplejsonrpc.client.generator.IdGenerator;
import com.github.arteam.simplejsonrpc.client.metadata.ClassMetadata;
import com.github.arteam.simplejsonrpc.client.metadata.MethodMetadata;
import com.github.arteam.simplejsonrpc.client.metadata.ParameterMetadata;
import com.github.arteam.simplejsonrpc.core.domain.ErrorMessage;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Date: 24.08.14
 * Time: 17:33
 * Proxy for accessing a remote JSON-RPC service trough an interface.
 */
public class ObjectApiBuilder extends AbstractBuilder implements InvocationHandler {

    @Nullable
    private ParamsType userParamsType;

    @Nullable
    private IdGenerator<?> userIdGenerator;

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
    public ObjectApiBuilder(Class<?> clazz, Transport transport, ObjectMapper mapper,
                            @Nullable ParamsType userParamsType, @Nullable IdGenerator<?> userIdGenerator) {
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
            if (returnType.getRawClass() == void.class) {
                return null;
            }
            return mapper.convertValue(result, returnType);
        } else {
            ErrorMessage errorMessage = mapper.treeToValue(error, ErrorMessage.class);
            throw new JsonRpcException(errorMessage);
        }
    }

    /**
     * Get request params in a JSON representation (map or array)
     */
    private JsonNode getParams(MethodMetadata method, @Nullable Object[] args,
                               ParamsType paramsType) {
        ObjectNode paramsAsMap = mapper.createObjectNode();
        ArrayNode paramsAsArray = mapper.createArrayNode();
        for (String paramName : method.getParams().keySet()) {
            ParameterMetadata parameterMetadata = method.getParams().get(paramName);
            int index = parameterMetadata.getIndex();
            JsonNode jsonArg = mapper.valueToTree(args != null ? args[index] : null);
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
    private String execute(ObjectNode request) {
        try {
            return transport.pass(mapper.writeValueAsString(request));
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Unable convert " + request + " to JSON", e);
        } catch (IOException e) {
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
    private ParamsType getParamsType(ClassMetadata classMetadata, MethodMetadata methodMetadata) {
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
