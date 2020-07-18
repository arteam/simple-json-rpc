package com.github.arteam.simplejsonrpc.server.metadata;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;

public interface ErrorDataResolver {

    Optional<Object> resolveData(Throwable throwable) throws Exception;

    class NullErrorDataResolver implements ErrorDataResolver {

        public static final ErrorDataResolver RESOLVER = new NullErrorDataResolver();

        private NullErrorDataResolver() {
        }

        @Override
        public Optional<Object> resolveData(Throwable throwable) {
            return Optional.empty();
        }

    }

    class FieldErrorDataResolver implements ErrorDataResolver {

        private final Field field;

        public FieldErrorDataResolver(Field field) {
            this.field = field;
        }

        @Override
        public Optional<Object> resolveData(Throwable throwable) throws Exception {
            return Optional.ofNullable(field.get(throwable));
        }

    }

    class MethodErrorDataResolver implements ErrorDataResolver {

        private final Method method;

        public MethodErrorDataResolver(Method method) {
            this.method = method;
        }

        @Override
        public Optional<Object> resolveData(Throwable throwable) throws Exception {
            return Optional.ofNullable(method.invoke(throwable));
        }

    }

}
