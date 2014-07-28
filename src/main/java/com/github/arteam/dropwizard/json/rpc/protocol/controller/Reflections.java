package com.github.arteam.dropwizard.json.rpc.protocol.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ContainerNode;
import com.github.arteam.dropwizard.json.rpc.protocol.domain.JsonRpcMethod;
import com.github.arteam.dropwizard.json.rpc.protocol.domain.JsonRpcParam;
import com.github.arteam.dropwizard.json.rpc.protocol.domain.Optional;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
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

    @Nullable
    public static Method findMethod(@NotNull Class clazz, @NotNull String name) {
        Class<?> searchType = clazz;
        while (searchType != null) {
            for (Method method : searchType.getDeclaredMethods()) {
                if (getAnnotation(method.getDeclaredAnnotations(), JsonRpcMethod.class) != null &&
                        Modifier.isPublic(method.getModifiers()) && name.equals(method.getName())) {
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
        int paramsSize = params.size();
        if (methodParams.length != paramsSize) {
            throw new IllegalArgumentException("Wrong amount arguments: " + paramsSize +
                    " for method: " + method.getName());
        }
        for (int i = 0; i < allParametersAnnotations.length; i++) {
            Annotation[] parameterAnnotations = allParametersAnnotations[i];
            JsonRpcParam jsonRpcParam = getAnnotation(parameterAnnotations, JsonRpcParam.class);
            if (jsonRpcParam == null) {
                throw new IllegalArgumentException("Unable get param name for " + i +
                        " parameter of method: " + method.getName());
            }
            JsonNode jsonNode = params.isObject() ? params.get(jsonRpcParam.value()) :   params.get(i);
            if (jsonNode == null && getAnnotation(parameterAnnotations, Optional.class) == null) {
                throw new IllegalArgumentException("Mandatory parameter: " + jsonRpcParam.value() + " of method: "
                        + method.getName() + " is not set");
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
