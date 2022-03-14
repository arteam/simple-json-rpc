package com.github.arteam.simplejsonrpc.server.metadata;

import java.util.Map;

/**
 * Date: 8/1/14
 * Time: 7:42 PM
 * <p>
 * Metadata about a Java class
 */
public record ClassMetadata(boolean service,
                            Map<String, MethodMetadata> methods) {
}
