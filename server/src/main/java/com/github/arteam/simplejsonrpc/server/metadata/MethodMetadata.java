package com.github.arteam.simplejsonrpc.server.metadata;

import java.lang.invoke.MethodHandle;
import java.util.Map;

/**
 * Date: 8/1/14
 * Time: 7:42 PM
 * <p>
 * Metadata about a Java method
 */
public record MethodMetadata(String name, MethodHandle methodHandle,
                             Map<String, ParameterMetadata> params) {
}
