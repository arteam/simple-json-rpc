package com.github.arteam.dropwizard.json.rpc.protocol.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ContainerNode;
import com.github.arteam.dropwizard.json.rpc.protocol.domain.JsonRpcMethod;
import com.github.arteam.dropwizard.json.rpc.protocol.domain.JsonRpcParam;
import com.github.arteam.dropwizard.json.rpc.protocol.domain.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Date: 07.06.14
 * Time: 14:42
 *
 * @author Artem Prigoda
 */
public final class Reflections {

    private static final Logger log = LoggerFactory.getLogger(JsonRpcController.class);


    @Nullable
    public static Method findMethod(@NotNull Class clazz, @NotNull String name) {
        // TODO Check methods params length for overloading
        Class<?> searchType = clazz;
        while (searchType != null) {
            for (Method method : searchType.getDeclaredMethods()) {
                if (name.equals(method.getName())) {
                    if (getAnnotation(method.getDeclaredAnnotations(), JsonRpcMethod.class) == null) {
                        log.warn("Annotation @JsonRpcMethod is not set for a method '" + method.getName() + "'");
                        continue;
                    }
                    if (!Modifier.isPublic(method.getModifiers())) {
                        log.warn("Method '" + method.getName() + "' is not public");
                        continue;
                    }
                    method.setAccessible(true);
                    return method;
                }
            }
            searchType = searchType.getSuperclass();
        }
        return null;
    }

    @NotNull
    public static JsonNode[] getMethodParams(@NotNull Method method,
                                             @NotNull ContainerNode<?> params) {
        Annotation[][] allParametersAnnotations = method.getParameterAnnotations();
        JsonNode[] methodParams = new JsonNode[allParametersAnnotations.length];
        if (methodParams.length != params.size()) {
            throw new IllegalArgumentException("Wrong amount arguments: " + params.size() +
                    " for a method '" + method.getName() + "'. Actual amount: " + methodParams.length);
        }
        for (int i = 0; i < allParametersAnnotations.length; i++) {
            Annotation[] parameterAnnotations = allParametersAnnotations[i];
            JsonRpcParam jsonRpcParam = getAnnotation(parameterAnnotations, JsonRpcParam.class);
            if (jsonRpcParam == null) {
                throw new IllegalArgumentException("Annotation @JsonRpcParam is not set for the " + i +
                        " parameter of a method '" + method.getName() + "'");
            }
            JsonNode jsonNode = params.isObject() ? params.get(jsonRpcParam.value()) : params.get(i);
            if (jsonNode == null && getAnnotation(parameterAnnotations, Optional.class) == null) {
                throw new IllegalArgumentException("Mandatory parameter '" + jsonRpcParam.value() + "' of a method '"
                        + method.getName() + "' is not set");
            }
            methodParams[i] = jsonNode;
        }
        return methodParams;
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public static <T extends Annotation> T getAnnotation(Annotation[] annotations, Class<T> clazz) {
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().equals(clazz)) {
                return (T) annotation;
            }
        }
        return null;
    }
}
