package com.github.arteam.simplejsonrpc.client.metadata;

import com.github.arteam.simplejsonrpc.client.ParamsType;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

/**
 * Date: 8/1/14
 * Time: 7:42 PM
 * <p>
 * Metadata about a Java method
 */
public class MethodMetadata {
    final private String name;
    final private @Nullable ParamsType paramsType;
    final private Map<String, ParameterMetadata> params;

//    public MethodMetadata(){}

    public MethodMetadata(String name,
                          @Nullable ParamsType paramsType,
                          Map<String, ParameterMetadata> params) {
        this.name = name;
        this.paramsType = paramsType;
        this.params = params;
    }

    public String name() {
        return name;
    }

    public @Nullable ParamsType paramsType() {
        return paramsType;
    }

    public Map<String, ParameterMetadata> params() {
        return params;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        MethodMetadata that = (MethodMetadata) obj;
        return Objects.equals(this.name, that.name) &&
                Objects.equals(this.paramsType, that.paramsType) &&
                Objects.equals(this.params, that.params);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, paramsType, params);
    }

    @Override
    public String toString() {
        return "MethodMetadata[" +
                "name=" + name + ", " +
                "paramsType=" + paramsType + ", " +
                "params=" + params + ']';
    }

}
