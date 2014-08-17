package com.github.arteam.simplejsonrpc.server.simple.exception;

import com.github.arteam.simplejsonrpc.core.annotation.JsonRpcError;

/**
 * Date: 7/31/14
 * Time: 6:23 PM
 *
 * @author Artem Prigoda
 */
@JsonRpcError(code = -32100, message = "Bad code")
public class BadCodeTeamServiceException extends RuntimeException {

    public BadCodeTeamServiceException(String message) {
        super(message);
    }
}
