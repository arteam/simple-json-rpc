package com.github.arteam.simplejsonrpc.server.simple.exception;

import com.github.arteam.simplejsonrpc.core.annotation.JsonRpcError;
import com.github.arteam.simplejsonrpc.core.annotation.JsonRpcErrorData;

@JsonRpcError(code = -30004, message = "Error with data (multiple getters)")
public class ExceptionWithDataMultipleMixed extends RuntimeException {

    @JsonRpcErrorData
    private final String[] data;

    public ExceptionWithDataMultipleMixed(String message, String[] data) {
        super(message);
        this.data = data;
    }

    @JsonRpcErrorData
    public String[] getData() {
        return data;
    }

}
