package com.github.arteam.json.rpc.simple.server;

import com.github.arteam.json.rpc.simple.annotation.JsonRpcMethod;
import com.github.arteam.json.rpc.simple.annotation.JsonRpcParam;
import com.github.arteam.json.rpc.simple.annotation.JsonRpcService;
import com.github.arteam.json.rpc.simple.annotation.Optional;
import com.github.arteam.json.rpc.simple.server.metadata.ClassMetadata;
import com.github.arteam.json.rpc.simple.server.metadata.MethodMetadata;
import com.github.arteam.json.rpc.simple.server.metadata.ParameterMetadata;
import com.google.common.collect.ImmutableMap;
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
                if (annotation.annotationType().equals(clazz)) {
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

        ImmutableMap.Builder<String, ParameterMetadata> parametersMetadata = ImmutableMap.builder();
        for (int i = 0; i < methodParamsSize; i++) {
            Annotation[] parameterAnnotations = allParametersAnnotations[i];
            JsonRpcParam jsonRpcParam = Reflections.getAnnotation(parameterAnnotations, JsonRpcParam.class);
            if (jsonRpcParam == null) {
                log.warn("Annotation @JsonRpcParam is not set for the " + i +
                        " parameter of a method '" + method.getName() + "'");
                return null;
            }

            Class<?> parameterType = parameterTypes[i];
            String paramName = jsonRpcParam.value();
            boolean optional = Reflections.getAnnotation(parameterAnnotations, Optional.class) != null;
            try {
                parametersMetadata.put(paramName,
                        new ParameterMetadata(paramName, parameterType, i, optional));
            } catch (IllegalArgumentException e) {
                log.error("Two parameters with the same name: " + paramName, e);
                return null;
            }
        }

        return parametersMetadata.build();
    }
}
