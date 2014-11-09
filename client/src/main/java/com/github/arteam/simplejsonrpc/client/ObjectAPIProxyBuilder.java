package com.github.arteam.simplejsonrpc.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.*;
import com.github.arteam.simplejsonrpc.client.builder.AbstractBuilder;
import com.github.arteam.simplejsonrpc.client.exception.JsonRpcException;
import com.github.arteam.simplejsonrpc.client.generator.CurrentTimeIdGenerator;
import com.github.arteam.simplejsonrpc.client.generator.IdGenerator;
import com.github.arteam.simplejsonrpc.core.annotation.JsonRpcMethod;
import com.github.arteam.simplejsonrpc.core.annotation.JsonRpcOptional;
import com.github.arteam.simplejsonrpc.core.annotation.JsonRpcParam;
import com.github.arteam.simplejsonrpc.core.annotation.JsonRpcService;
import com.github.arteam.simplejsonrpc.core.domain.ErrorMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Date: 24.08.14
 * Time: 17:33
 *
 * @author Artem Prigoda
 */
public class ObjectAPIProxyBuilder extends AbstractBuilder implements InvocationHandler {

    private static final String RESULT = "result";
    private static final String ERROR = "error";

    @Nullable
    private IdGenerator userIdGenerator;

    @Nullable
    private ParamsType userParamsType;

    public ObjectAPIProxyBuilder(Transport transport, ObjectMapper mapper, ParamsType userParamsType, IdGenerator userIdGenerator) {
        super(transport, mapper);
        this.userParamsType = userParamsType;
        this.userIdGenerator = userIdGenerator;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // Check that it's a service
        Class<?> declaringClass = method.getDeclaringClass();
        Annotation[] classAnnotations = declaringClass.getDeclaredAnnotations();
        JsonRpcService rpcServiceAnn = getAnnotation(classAnnotations, JsonRpcService.class);
        if (rpcServiceAnn == null) {
            throw new IllegalStateException("Class '" + declaringClass.getCanonicalName() +
                    "' is not annotated as @JsonRpcService");
        }

        // Check that it's a JSON-RPC method
        Annotation[] methodAnnotations = method.getDeclaredAnnotations();
        JsonRpcMethod rpcMethodAnn = getAnnotation(methodAnnotations, JsonRpcMethod.class);
        if (rpcMethodAnn == null) {
            throw new IllegalStateException("Method '" + method.getName() + "' is not annotated as @JsonRpcMethod");
        }


        // Get method name (annotation or the actual name), params and id generator
        String methodName = !rpcMethodAnn.value().isEmpty() ? rpcMethodAnn.value() : method.getName();
        JsonNode params = getParams(method, args, getParamsType(classAnnotations, methodAnnotations));
        IdGenerator<?> idGenerator = getIdGenerator(classAnnotations);

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
     * Get actual id generator
     */
    private IdGenerator<?> getIdGenerator(Annotation[] classAnnotations) {
        if (userIdGenerator != null) {
            return userIdGenerator;
        }

        JsonRpcId jsonRpcIdAnn = getAnnotation(classAnnotations, JsonRpcId.class);
        // TODO change to AtomicLongIdGenerator as a default choice
        Class<? extends IdGenerator<?>> idGeneratorClazz = (jsonRpcIdAnn == null) ?
                CurrentTimeIdGenerator.class : jsonRpcIdAnn.value();
        try {
            return idGeneratorClazz.newInstance();
        } catch (Exception e) {
            throw new IllegalStateException("Unable instantiate id generator: " + idGeneratorClazz, e);
        }
    }

    private ParamsType getParamsType(Annotation[] classAnnotations, Annotation[] methodAnnotations) {
        if (userParamsType != null) {
            return userParamsType;
        }
        JsonRpcParams rpcParamsAnn = getAnnotation(methodAnnotations, JsonRpcParams.class);
        if (rpcParamsAnn == null) {
            rpcParamsAnn = getAnnotation(classAnnotations, JsonRpcParams.class);
        }
        return rpcParamsAnn != null ? rpcParamsAnn.value() : ParamsType.MAP;

    }

    /**
     * Get request params in JSON representation (map or array)
     */
    private JsonNode getParams(Method method, Object[] args, ParamsType paramsType) {
        ObjectNode paramsAsMap = mapper.createObjectNode();
        ArrayNode paramsAsArray = mapper.createArrayNode();
        Annotation[][] parametersAnnotations = method.getParameterAnnotations();
        for (int i = 0; i < parametersAnnotations.length; i++) {
            // Check that it's a JSON-RPC param
            JsonRpcParam rpcParamAnn = getAnnotation(parametersAnnotations[i], JsonRpcParam.class);
            if (rpcParamAnn == null) {
                throw new IllegalStateException("Parameter with index=" + i + " of method '" + method.getName() +
                        "' is not annotated with @JsonRpcParam");
            }
            JsonNode jsonArg = mapper.valueToTree(args[i]);
            if (jsonArg == null || jsonArg == NullNode.instance) {
                if (getAnnotation(parametersAnnotations[i], JsonRpcOptional.class) != null) {
                    if (paramsType == ParamsType.ARRAY) {
                        paramsAsArray.add(NullNode.instance);
                    }
                } else {
                    throw new IllegalArgumentException("Parameter '" + rpcParamAnn.value() +
                            "' of method '" + method.getName() + "' is mandatory and can't be null");
                }
            } else {
                if (paramsType == ParamsType.MAP) {
                    paramsAsMap.set(rpcParamAnn.value(), jsonArg);
                } else if (paramsType == ParamsType.ARRAY) {
                    paramsAsArray.add(jsonArg);
                }
            }
        }
        return paramsType == ParamsType.MAP ? paramsAsMap : paramsAsArray;
    }

    private String execute(ObjectNode request) {
        String textRequest;
        try {
            textRequest = mapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Unable convert " + request + " to JSON", e);
        }
        try {
            return transport.pass(textRequest);
        } catch (IOException e) {
            throw new IllegalStateException("I/O error during request processing", e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends Annotation> T getAnnotation(@Nullable Annotation[] annotations,
                                                          @NotNull Class<T> clazz) {
        if (annotations != null) {
            for (Annotation annotation : annotations) {
                if (annotation.annotationType().equals(clazz)) {
                    return (T) annotation;
                }
            }
        }
        return null;
    }
}
