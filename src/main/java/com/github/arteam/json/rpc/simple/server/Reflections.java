package com.github.arteam.json.rpc.simple.server;

import com.github.arteam.json.rpc.simple.annotation.JsonRpcMethod;
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
 * <p/>
 * Reflection utils for scanning service metadata
 *
 * @author Artem Prigoda
 */
class Reflections {

    private static final Logger log = LoggerFactory.getLogger(JsonRpcServer.class);

    /**
     * Finds an appropriate method by it's name or a name in a {@link JsonRpcMethod} annotation.
     * <p/>
     * Methods should be public and non-static and be exposed as a web-method by placing on
     * it a {@link JsonRpcMethod} annotation.
     *
     * @param clazz service class
     * @param name  RPC method name
     * @return method metadata or {@code null} if it wasn't found
     */
    @Nullable
    public static Method findMethod(@NotNull Class clazz, @NotNull String name) {
        Class<?> searchType = clazz;
        // Search through the class hierarchy
        while (searchType != null) {
            for (Method method : searchType.getDeclaredMethods()) {
                String methodName = method.getName();
                // Checks the annotation
                JsonRpcMethod jsonRpcMethod = getAnnotation(method.getDeclaredAnnotations(), JsonRpcMethod.class);
                if (jsonRpcMethod == null) {
                    if (name.equals(methodName)) {
                        log.warn("Annotation @JsonRpcMethod is not set for a method '" + methodName + "'");
                    }
                    continue;
                }

                String rpcMethodName = !jsonRpcMethod.value().isEmpty() ? jsonRpcMethod.value() : methodName;
                if (name.equals(rpcMethodName)) {
                    // Check modifiers, only public non-static methods are permitted
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


    /**
     * Finds an entity annotation with appropriate type.
     *
     * @param annotations entity annotations
     * @param clazz       annotation class type
     * @param <T>         actual compile-time annotation type
     * @return appropriate annotation or {@code null} if it wasn't found
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public static <T extends Annotation> T getAnnotation(@NotNull Annotation[] annotations,
                                                         @NotNull Class<T> clazz) {
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().equals(clazz)) {
                return (T) annotation;
            }
        }
        return null;
    }
}
