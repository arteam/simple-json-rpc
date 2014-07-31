package com.github.arteam.json.rpc.simple.exception;

import com.github.arteam.json.rpc.simple.annotation.JsonRpcError;

/**
 * Date: 7/31/14
 * Time: 6:23 PM
 *
 * @author Artem Prigoda
 */
@JsonRpcError(code = -32000)
public class EmptyMessageTeamServiceException extends RuntimeException {

    public EmptyMessageTeamServiceException(String message) {
        super(message);
    }
}
