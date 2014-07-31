package com.github.arteam.json.rpc.simple.exception;

import com.github.arteam.json.rpc.simple.annotation.JsonRpcError;

/**
 * Date: 7/31/14
 * Time: 6:23 PM
 *
 * @author Artem Prigoda
 */
@JsonRpcError(code = -32032, message = "You are not authorized to the team service")
public class TeamServiceAuthException extends RuntimeException {

    public TeamServiceAuthException(String message) {
        super(message);
    }
}
