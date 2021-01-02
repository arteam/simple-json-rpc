package com.github.arteam.simplejsonrpc.server.metadata;

public class ErrorMetadata {
    final int code;
    final String message;

    public ErrorMetadata(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
