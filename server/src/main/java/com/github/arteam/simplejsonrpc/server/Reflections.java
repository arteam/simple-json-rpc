package com.github.arteam.simplejsonrpc.server;

import com.github.arteam.simplejsonrpc.core.annotation.JsonRpcErrorData;
import com.github.arteam.simplejsonrpc.core.annotation.JsonRpcMethod;
import com.github.arteam.simplejsonrpc.core.annotation.JsonRpcOptional;
import com.github.arteam.simplejsonrpc.core.annotation.JsonRpcParam;
import com.github.arteam.simplejsonrpc.core.annotation.JsonRpcService;
import com.github.arteam.simplejsonrpc.server.metadata.ClassMetadata;
import com.github.arteam.simplejsonrpc.server.metadata.ErrorDataResolver;
import com.github.arteam.simplejsonrpc.server.metadata.MethodMetadata;
import com.github.arteam.simplejsonrpc.server.metadata.ParameterMetadata;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Date: 07.06.14
 * Time: 14:42
 * <p>Reflection utils for scanning service metadata</p>
 */
class Reflections {

    private static final Logger log = LoggerFactory.getLogger(JsonRpcServer.class);
    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

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
    public static <T extends Annotation> T getAnnotation(@Nullable Annotation[] annotations, Class<T> clazz) {
        if (annotations != null) {
            for (Annotation annotation : annotations) {
                if (annotation != null && annotation.annotationType().equals(clazz)) {
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
    public static ClassMetadata getClassMetadata(Class<?> clazz) {
        Map<String, MethodMetadata> methodsMetadata = new HashMap<>();
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
                Map<String, ParameterMetadata> methodParams = getMethodParameters(method);
                if (methodParams == null) {
                    log.warn("Method '" + methodName + "' has misconfigured parameters");
                    continue;
                }

                MethodHandle methodHandle;
                try {
                    methodHandle = MethodHandles.privateLookupIn(method.getDeclaringClass(), LOOKUP)
                            .unreflect(method);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                MethodMetadata oldMethodMetadata = methodsMetadata.put(rpcMethodName,
                        new MethodMetadata(rpcMethodName, methodHandle, methodParams));
                if (oldMethodMetadata != null) {
                    // Throw exception, because two methods with the same name leads to unexpected behaviour
                    throw new IllegalArgumentException("There two methods with the same name in " + clazz);
                }
            }
            searchType = searchType.getSuperclass();
        }

        boolean isService = getAnnotation(clazz != null ? clazz.getAnnotations() : null, JsonRpcService.class) != null;
        return new ClassMetadata(isService, Map.copyOf(methodsMetadata));
    }

    /**
     * Gets JSON-RPC meta-information about method parameters.
     *
     * @param method actual method
     * @return map of parameters metadata by their names
     */
    @Nullable
    private static Map<String, ParameterMetadata> getMethodParameters(Method method) {
        Annotation[][] allParametersAnnotations = method.getParameterAnnotations();
        int methodParamsSize = allParametersAnnotations.length;
        Class<?>[] parameterTypes = method.getParameterTypes();
        Type[] genericParameterTypes = method.getGenericParameterTypes();

        Map<String, ParameterMetadata> parametersMetadata = new HashMap<>();
        for (int i = 0; i < methodParamsSize; i++) {
            Annotation[] parameterAnnotations = allParametersAnnotations[i];
            JsonRpcParam jsonRpcParam = Reflections.getAnnotation(parameterAnnotations, JsonRpcParam.class);
            if (jsonRpcParam == null) {
                log.warn("Annotation @JsonRpcParam is not set for the " + i +
                        " parameter of a method '" + method.getName() + "'");
                return null;
            }

            String paramName = jsonRpcParam.value();
            boolean optional = Reflections.getAnnotation(parameterAnnotations, JsonRpcOptional.class) != null;
            ParameterMetadata oldParameterMetadata = parametersMetadata.put(paramName,
                    new ParameterMetadata(paramName, parameterTypes[i], genericParameterTypes[i], i, optional));
            if (oldParameterMetadata != null) {
                log.error("There two parameters with the same name in method '" + method.getName() +
                        "' of the class '" + method.getDeclaringClass() + "'");
                return null;
            }
        }
        return Map.copyOf(parametersMetadata);
    }

    static ErrorDataResolver buildErrorDataResolver(Class<? extends Throwable> throwableClass) {
        Class<?> c = throwableClass;
        VarHandle dataField = null;
        MethodHandle dataMethod = null;
        while (c != null) {
            for (Field field : c.getDeclaredFields()) {
                if (field.isAnnotationPresent(JsonRpcErrorData.class)) {
                    if (dataField != null) {
                        throw new IllegalArgumentException("Ambiguous configuration: there is more than one " +
                                "@JsonRpcErrorData annotated property in " + c.getName());
                    }
                    try {
                        dataField = MethodHandles.privateLookupIn(field.getDeclaringClass(), LOOKUP)
                                .unreflectVarHandle(field);
                    } catch (IllegalAccessException e) {
                        throw new IllegalStateException(e);
                    }
                }
            }
            for (Method method : c.getDeclaredMethods()) {
                if (method.isAnnotationPresent(JsonRpcErrorData.class)) {
                    if (method.getReturnType() == void.class) {
                        log.warn("Method '{}' annotated with 'JsonRpcErrorData' cannot have void return type", method.getName());
                        continue;
                    }
                    if (method.getParameterCount() > 0) {
                        log.warn("Method '{}' annotated with 'JsonRpcErrorData' must be with zero arguments", method.getName());
                        continue;
                    }
                    if (dataField != null || dataMethod != null) {
                        throw new IllegalArgumentException("Ambiguous configuration: there is more than one " +
                                "@JsonRpcErrorData annotated property in " + c.getName());
                    }
                    try {
                        dataMethod = MethodHandles.privateLookupIn(method.getDeclaringClass(), LOOKUP)
                                .unreflect(method);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            c = c.getSuperclass();
        }
        if (dataField != null) {
            return new ThrowableErrorResolver(dataField::get);
        } else if (dataMethod != null) {
            return new ThrowableErrorResolver(dataMethod::invoke);
        } else {
            return t -> Optional.empty();
        }
    }

    @FunctionalInterface
    private interface DataFunction {
        Object get(Throwable t) throws Throwable;
    }

    private static final class ThrowableErrorResolver implements ErrorDataResolver {
        private final DataFunction function;

        private ThrowableErrorResolver(DataFunction function) {
            this.function = function;
        }

            @Override
            public Optional<Object> resolveData(Throwable throwable) throws Exception {
                try {
                    return Optional.ofNullable(function.get(throwable));
                } catch (Throwable e) {
                    throw new IllegalStateException(e);
                }
            }

        public DataFunction function() {
            return function;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (ThrowableErrorResolver) obj;
            return Objects.equals(this.function, that.function);
        }

        @Override
        public int hashCode() {
            return Objects.hash(function);
        }

        @Override
        public String toString() {
            return "ThrowableErrorResolver[" +
                    "function=" + function + ']';
        }

        }
}
