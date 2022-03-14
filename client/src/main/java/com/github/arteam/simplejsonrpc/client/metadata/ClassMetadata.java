package com.github.arteam.simplejsonrpc.client.metadata;

import com.github.arteam.simplejsonrpc.client.ParamsType;
import com.github.arteam.simplejsonrpc.client.generator.IdGenerator;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Date: 8/1/14
 * Time: 7:42 PM
 * <p>
 * Metadata about a Java class
 */
public record ClassMetadata(@Nullable ParamsType paramsType, IdGenerator<?> idGenerator,
                            Map<Method, MethodMetadata> methods) {
}
