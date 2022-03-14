package com.github.arteam.simplejsonrpc.core.domain;

import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ValueNode;

/**
 * Date: 07.06.14
 * Time: 12:35
 * <p>Representation of a JSON-RPC error response</p>
 */
public record ErrorResponse(ValueNode id,
                            ErrorMessage error,
                            String jsonrpc) implements Response {

    public static final String VERSION = "2.0";

    public static ErrorResponse of(ErrorMessage error) {
        return new ErrorResponse(NullNode.getInstance(), error, VERSION);
    }

    public static ErrorResponse of(ValueNode id, ErrorMessage error) {
        return new ErrorResponse(id, error, VERSION);
    }
}
