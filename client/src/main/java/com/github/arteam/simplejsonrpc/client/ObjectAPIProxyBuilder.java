package com.github.arteam.simplejsonrpc.client;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.POJONode;
import com.github.arteam.simplejsonrpc.client.generator.CurrentTimeIdGenerator;
import com.github.arteam.simplejsonrpc.client.generator.IdGenerator;
import com.github.arteam.simplejsonrpc.core.annotation.JsonRpcMethod;
import com.github.arteam.simplejsonrpc.core.annotation.JsonRpcParam;
import com.github.arteam.simplejsonrpc.core.annotation.JsonRpcService;
import com.github.arteam.simplejsonrpc.core.domain.ErrorMessage;
import com.github.arteam.simplejsonrpc.core.domain.Request;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
public class ObjectAPIProxyBuilder implements InvocationHandler {

    private static final String RESULT = "result";
    private static final String ERROR = "error";

    private Transport transport;

    private ObjectMapper mapper;

    private IdGenerator userIdGenerator;

    public ObjectAPIProxyBuilder(Transport transport, ObjectMapper mapper) {
        this.transport = transport;
        this.mapper = mapper;
    }

    public ObjectAPIProxyBuilder(Transport transport, ObjectMapper mapper, IdGenerator userIdGenerator) {
        this.transport = transport;
        this.mapper = mapper;
        this.userIdGenerator = userIdGenerator;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        JsonRpcService rpcService = getAnnotation(method.getDeclaringClass().getDeclaredAnnotations(), JsonRpcService.class);
        if (rpcService == null) {
            throw new IllegalArgumentException("Not a JSON-RPC service");
        }

        JsonRpcMethod jsonRpcMethod = getAnnotation(method.getDeclaredAnnotations(), JsonRpcMethod.class);
        if (jsonRpcMethod == null) {
            throw new IllegalArgumentException(method + " is not annotated");
        }
        String methodName = !jsonRpcMethod.value().isEmpty() ? jsonRpcMethod.value() : method.getName();

        ObjectNode objectNode = mapper.createObjectNode();
        Annotation[][] allParametersAnnotations = method.getParameterAnnotations();
        for (int i = 0; i < allParametersAnnotations.length; i++) {
            Annotation[] parameterAnnotations = allParametersAnnotations[i];
            JsonRpcParam jsonRpcParam = getAnnotation(parameterAnnotations, JsonRpcParam.class);
            if (jsonRpcParam != null) {
                // TODO check required
                objectNode.set(jsonRpcParam.value(), mapper.valueToTree(args[i]));
            }
        }

        IdGenerator<?> idGenerator;
        if (userIdGenerator != null) {
            idGenerator = userIdGenerator;
        } else {
            JsonRpcId jsonRpcId = getAnnotation(method.getDeclaringClass().getDeclaredAnnotations(), JsonRpcId.class);
            Class<? extends IdGenerator<?>> idGeneratorClazz = jsonRpcId == null ? CurrentTimeIdGenerator.class : jsonRpcId.value();
            idGenerator = idGeneratorClazz.newInstance();
        }

        Request request = new Request("2.0", methodName, objectNode, new POJONode(idGenerator.generate()));
        String textRequest = mapper.writeValueAsString(request);
        String textResponse = transport.pass(textRequest);

        JsonNode responseNode = mapper.readTree(textResponse);
        JsonNode result = responseNode.get(RESULT);
        JsonNode error = responseNode.get(ERROR);

        if (result != null) {
            Type genericReturnType = method.getGenericReturnType();
            JavaType toValueType = mapper.getTypeFactory().constructType(genericReturnType);
            return mapper.convertValue(result, toValueType);
        } else {
            ErrorMessage errorMessage = mapper.treeToValue(error, ErrorMessage.class);
            throw new JsonRpcException(errorMessage);
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
