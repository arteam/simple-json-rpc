package com.github.arteam.simplejsonrpc.client.metadata;

import com.github.arteam.simplejsonrpc.client.ParamsType;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Date: 8/1/14
 * Time: 7:42 PM
 * <p>
 * Metadata about a Java method
 */
public record MethodMetadata(String name,
                             @Nullable ParamsType paramsType,
                             Map<String, ParameterMetadata> params) {
}
