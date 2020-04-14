package com.github.arteam.simplejsonrpc.server;

import com.github.arteam.simplejsonrpc.core.annotation.*;
import com.github.arteam.simplejsonrpc.server.metadata.ClassMetadata;
import com.github.arteam.simplejsonrpc.server.metadata.MethodMetadata;
import com.github.arteam.simplejsonrpc.server.metadata.ParameterMetadata;
import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.Objects;

/**
 * Date: 07.06.14
 * Time: 14:42
 * <p/>
 * Reflection utils for scanning service metadata
 */
class Reflections {

    private static final Logger log = LoggerFactory.getLogger(JsonRpcServer.class);

    private Reflections() {
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
    public static <T extends Annotation> T getAnnotation(@Nullable Annotation[] annotations,
                                                         @NotNull Class<T> clazz) {
        if (annotations != null) {
            for (Annotation annotation : annotations) {
                if (Objects.requireNonNull(annotation).annotationType().equals(clazz)) {
                    return (T) annotation;
                }
            }
        }
        return null;
    }

    /**
     * Gets class metadata for JSON-RPC processing.
     * It scans the class and builds JSON-RPC meta-information about methods and it's parameters
     *
     * @param clazz actual service class
     * @return service class JSON-RPC meta-information
     */
    @NotNull
    public static ClassMetadata getClassMetadata(@NotNull Class<?> clazz) {
        ImmutableMap.Builder<String, MethodMetadata> methodsMetadata = ImmutableMap.builder();

        Class<?> searchType = clazz;
        // Search through the class hierarchy
        while (searchType != null) {
            for (Method method : searchType.getDeclaredMethods()) {
                String methodName = method.getName();
                // Checks the annotation
                JsonRpcMethod jsonRpcMethod = getAnnotation(method.getDeclaredAnnotations(), JsonRpcMethod.class);
                if (jsonRpcMethod == null) {
                    continue;
                }

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

                String rpcMethodName = !jsonRpcMethod.value().isEmpty() ? jsonRpcMethod.value() : methodName;
                ImmutableMap<String, ParameterMetadata> methodParams = getMethodParameters(method);
                if (methodParams == null) {
                    log.warn("Method '" + methodName + "' has misconfigured parameters");
                    continue;
                }

                method.setAccessible(true);
                methodsMetadata.put(rpcMethodName, new MethodMetadata(rpcMethodName, method, methodParams));
            }
            searchType = searchType.getSuperclass();
        }

        boolean isService = getAnnotation(clazz.getAnnotations(), JsonRpcService.class) != null;
        try {
            return new ClassMetadata(isService, methodsMetadata.build());
        } catch (IllegalArgumentException e) {
            // Throw exception, because two methods with the same name leads to unexpected behaviour
            throw new IllegalArgumentException("There two methods with the same name in " + clazz, e);
        }
    }

    /**
     * Gets JSON-RPC meta-information about method parameters.
     *
     * @param method actual method
     * @return map of parameters metadata by their names
     */
    @Nullable
    private static ImmutableMap<String, ParameterMetadata> getMethodParameters(@NotNull Method method) {
        Annotation[][] allParametersAnnotations = method.getParameterAnnotations();
        int methodParamsSize = allParametersAnnotations.length;
        Class<?>[] parameterTypes = method.getParameterTypes();
        Type[] genericParameterTypes = method.getGenericParameterTypes();

        ImmutableMap.Builder<String, ParameterMetadata> parametersMetadata = ImmutableMap.builder();
        for (int i = 0; i < methodParamsSize; i++) {
            Annotation[] parameterAnnotations = allParametersAnnotations[i];
            JsonRpcParam jsonRpcParam = Reflections.getAnnotation(parameterAnnotations, JsonRpcParam.class);
            JsonRpcId jsonRpcId = Reflections.getAnnotation(parameterAnnotations, JsonRpcId.class);
            if (jsonRpcParam != null && jsonRpcId != null) {
                log.warn("Only one annotation @JsonRpcParam or @JsonRpcParam must be set for the " + i +
                        " parameter of a method '" + method.getName() + "'");
                return null;
            }
            if (jsonRpcParam == null && jsonRpcId == null) {
                log.warn("Annotation @JsonRpcParam or @JsonRpcParam is not set for the " + i +
                        " parameter of a method '" + method.getName() + "'");
                return null;
            }
            String paramName = null;
            boolean isId = false;
            if (jsonRpcParam != null) {
                paramName = jsonRpcParam.value();
            }
            if (jsonRpcId != null) {
                paramName = ParameterMetadata.ID_NAME;
                isId = true;
            }
            boolean optional = Reflections.getAnnotation(parameterAnnotations, JsonRpcOptional.class) != null;
            parametersMetadata.put(paramName, new ParameterMetadata(paramName, parameterTypes[i],
                    genericParameterTypes[i], i, optional, isId));
        }

        try {
            return parametersMetadata.build();
        } catch (IllegalArgumentException e) {
            log.error("There two parameters with the same name in method '" + method.getName() +
                    "' of the class '" + method.getDeclaringClass() + "'", e);
            return null;
        }
    }
}
