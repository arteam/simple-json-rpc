package com.github.arteam.simplejsonrpc.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NumericNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.POJONode;
import com.fasterxml.jackson.databind.node.ValueNode;
import com.github.arteam.simplejsonrpc.client.builder.AbstractBuilder;
import com.github.arteam.simplejsonrpc.client.exception.JsonRpcException;
import com.github.arteam.simplejsonrpc.client.generator.CurrentTimeIdGenerator;
import com.github.arteam.simplejsonrpc.client.generator.IdGenerator;
import com.github.arteam.simplejsonrpc.core.annotation.JsonRpcMethod;
import com.github.arteam.simplejsonrpc.core.annotation.JsonRpcParam;
import com.github.arteam.simplejsonrpc.core.annotation.JsonRpcService;
import com.github.arteam.simplejsonrpc.core.domain.ErrorMessage;
import com.github.arteam.simplejsonrpc.core.domain.Request;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * Date: 24.08.14
 * Time: 17:33
 *
 * @author Artem Prigoda
 */
public class ObjectAPIProxyBuilder extends AbstractBuilder implements InvocationHandler {

    private static final String RESULT = "result";
    private static final String ERROR = "error";

    private IdGenerator userIdGenerator;

    public ObjectAPIProxyBuilder(Transport transport, ObjectMapper mapper, IdGenerator userIdGenerator) {
        super(transport, mapper);
        this.userIdGenerator = userIdGenerator;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // Check it's a service
        Annotation[] classAnnotations = method.getDeclaringClass().getDeclaredAnnotations();
        JsonRpcService rpcServiceAnn = getAnnotation(classAnnotations, JsonRpcService.class);
        if (rpcServiceAnn == null) {
            throw new IllegalArgumentException("Not a JSON-RPC service");
        }

        // Check that it's a JSON-RPC method
        Annotation[] methodAnnotations = method.getDeclaredAnnotations();
        JsonRpcMethod rpcMethodAnn = getAnnotation(methodAnnotations, JsonRpcMethod.class);
        if (rpcMethodAnn == null) {
            throw new IllegalArgumentException(method + " is not annotated");
        }

        // Get method name (annotation or the actual name)
        String methodName = !rpcMethodAnn.value().isEmpty() ? rpcMethodAnn.value() : method.getName();

        // Build params as object map
        // TODO param for constructing array params?
        ObjectNode params = mapper.createObjectNode();
        Annotation[][] parametersAnnotations = method.getParameterAnnotations();
        for (int i = 0; i < parametersAnnotations.length; i++) {
            // Check that it's a JSON-RPC param
            JsonRpcParam rpcParamAnn = getAnnotation(parametersAnnotations[i], JsonRpcParam.class);
            if (rpcParamAnn != null) {
                // TODO Type check required
                // TODO Handle optional params
                params.set(rpcParamAnn.value(), mapper.valueToTree(args[i]));
            }
        }

        // Get id generator
        IdGenerator<?> idGenerator;
        if (userIdGenerator != null) {
            idGenerator = userIdGenerator;
        } else {
            JsonRpcId jsonRpcIdAnn = getAnnotation(classAnnotations, JsonRpcId.class);
            // TODO change to AtomicLongIdGenerator as a default choice
            Class<? extends IdGenerator<?>> idGeneratorClazz = (jsonRpcIdAnn == null) ?
                    CurrentTimeIdGenerator.class :   jsonRpcIdAnn.value();
            idGenerator = idGeneratorClazz.newInstance();
        }


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
