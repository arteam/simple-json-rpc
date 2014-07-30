package com.github.arteam.dropwizard.json.rpc.protocol.controller;

import com.github.arteam.dropwizard.json.rpc.protocol.domain.JsonRpcMethod;
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
class Reflections {

    private static final Logger log = LoggerFactory.getLogger(JsonRpcServer.class);

    @Nullable
    public static Method findMethod(@NotNull Class clazz, @NotNull String name) {
        // TODO Check methods params length for overloading
        Class<?> searchType = clazz;
        while (searchType != null) {
            for (Method method : searchType.getDeclaredMethods()) {
                String methodName = method.getName();
                JsonRpcMethod jsonRpcMethod = getAnnotation(method.getDeclaredAnnotations(), JsonRpcMethod.class);
                if (jsonRpcMethod == null) {
                    if (name.equals(methodName)) {
                        log.warn("Annotation @JsonRpcMethod is not set for a method '" + methodName + "'");
                    }
                    continue;
                }

                String rpcMethodName = !jsonRpcMethod.value().isEmpty() ? jsonRpcMethod.value() : methodName;
                if (name.equals(rpcMethodName)) {
                    int modifiers = method.getModifiers();
                    if (!Modifier.isPublic(modifiers)) {
                        log.warn("Method '" + methodName + "' is not public");
                        continue;
                    }
                    if (Modifier.isStatic(modifiers)) {
                        log.warn("Method '" + methodName + "' is static");
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
