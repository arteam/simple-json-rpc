package com.github.arteam.simplejsonrpc.server.simple.exception;

import com.github.arteam.simplejsonrpc.core.annotation.JsonRpcError;

/**
 * Date: 7/31/14
 * Time: 6:23 PM
 */
@JsonRpcError(code = -32032, message = "You are not authorized to the team service")
public class TeamServiceAuthException extends RuntimeException {

    public TeamServiceAuthException(String message) {
        super(message);
    }
}
