package com.github.arteam.simplejsonrpc.server.simple.exception;

import com.github.arteam.simplejsonrpc.core.annotation.JsonRpcError;

/**
 * Date: 7/31/14
 * Time: 6:23 PM
 */
@JsonRpcError(code = -32000)
public class EmptyMessageTeamServiceException extends RuntimeException {

    public EmptyMessageTeamServiceException(String message) {
        super(message);
    }
}
