package com.github.arteam.simplejsonrpc.client.metadata;

import com.github.arteam.simplejsonrpc.client.ParamsType;
import com.github.arteam.simplejsonrpc.client.generator.IdGenerator;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;

/**
 * Date: 8/1/14
 * Time: 7:42 PM
 * <p>
 * Metadata about a Java class
 */
public class ClassMetadata {
    private @Nullable ParamsType paramsType;
    private IdGenerator<?> idGenerator;
    private Map<Method, MethodMetadata> methods;

    public ClassMetadata(){}

    public ClassMetadata(@Nullable ParamsType paramsType, IdGenerator<?> idGenerator,
                         Map<Method, MethodMetadata> methods) {
        this.paramsType = paramsType;
        this.idGenerator = idGenerator;
        this.methods = methods;
    }

    public @Nullable ParamsType paramsType() {
        return paramsType;
    }

    public IdGenerator<?> idGenerator() {
        return idGenerator;
    }

    public Map<Method, MethodMetadata> methods() {
        return methods;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        ClassMetadata that = (ClassMetadata) obj;
        return Objects.equals(this.paramsType, that.paramsType) &&
                Objects.equals(this.idGenerator, that.idGenerator) &&
                Objects.equals(this.methods, that.methods);
    }

    @Override
    public int hashCode() {
        return Objects.hash(paramsType, idGenerator, methods);
    }

    @Override
    public String toString() {
        return "ClassMetadata[" +
                "paramsType=" + paramsType + ", " +
                "idGenerator=" + idGenerator + ", " +
                "methods=" + methods + ']';
    }

}
