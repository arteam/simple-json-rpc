package com.github.arteam.simplejsonrpc.server.simple.exception;

import com.github.arteam.simplejsonrpc.core.annotation.JsonRpcError;
import com.github.arteam.simplejsonrpc.core.annotation.JsonRpcErrorData;

@JsonRpcError(code = -30002, message = "Error with data (multiple fields)")
public class ExceptionWithDataMultipleFields extends RuntimeException {

    @JsonRpcErrorData
    private final String[] data;
    @JsonRpcErrorData
    private final String anotherData;

    public ExceptionWithDataMultipleFields(String message, String[] data, String anotherData) {
        super(message);
        this.data = data;
        this.anotherData = anotherData;
    }

}
