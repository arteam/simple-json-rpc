package com.github.arteam.simplejsonrpc.client;

import com.github.arteam.simplejsonrpc.core.domain.ErrorMessage;

/**
 * Date: 8/9/14
 * Time: 10:08 PM
 *
 * @author Artem Prigoda
 */
public class JsonRpcException extends RuntimeException {

    private ErrorMessage errorMessage;

    public JsonRpcException(ErrorMessage errorMessage) {
        super(errorMessage.toString());
        this.errorMessage = errorMessage;
    }

    public ErrorMessage getErrorMessage() {
        return errorMessage;
    }
}
