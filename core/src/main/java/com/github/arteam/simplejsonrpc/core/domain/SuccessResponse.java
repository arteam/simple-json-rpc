package com.github.arteam.simplejsonrpc.core.domain;

import com.fasterxml.jackson.databind.node.ValueNode;
import org.jetbrains.annotations.Nullable;

/**
 * Date: 07.06.14
 * Time: 12:31
 * Representation of a successful JSON-RPC response
 */
public record SuccessResponse(ValueNode id,
                              @Nullable Object result,
                              String jsonrpc) implements Response {
    public static final String VERSION = "2.0";
}
