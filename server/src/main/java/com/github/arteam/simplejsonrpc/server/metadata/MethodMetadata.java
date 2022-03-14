package com.github.arteam.simplejsonrpc.server.metadata;

import com.google.common.collect.ImmutableMap;

import java.lang.invoke.MethodHandle;

/**
 * Date: 8/1/14
 * Time: 7:42 PM
 * <p>
 * Metadata about a Java method
 */
public record MethodMetadata(String name, MethodHandle methodHandle,
                             ImmutableMap<String, ParameterMetadata> params) {
}
