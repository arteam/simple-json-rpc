package com.github.arteam.simplejsonrpc.server.metadata;

import java.lang.reflect.Type;

/**
 * Date: 8/1/14
 * Time: 7:44 PM
 * <p>
 * Method parameter metadata
 */
public record ParameterMetadata(String name, Class<?> type, Type genericType,
                                int index, boolean optional) {
}
