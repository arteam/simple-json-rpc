package com.github.arteam.simplejsonrpc.server.simple.exception;

import com.github.arteam.simplejsonrpc.core.annotation.JsonRpcError;
import com.github.arteam.simplejsonrpc.core.annotation.JsonRpcErrorData;

@JsonRpcError(code = -30001, message = "Error with data (getter)")
public class ExceptionWithDataGetter extends RuntimeException {

    private final String[] data;

    public ExceptionWithDataGetter(String message, String[] data) {
        super(message);
        this.data = data;
    }

    @JsonRpcErrorData
    public String[] getData() {
        return data;
    }
}
