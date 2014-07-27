package com.github.arteam.dropwizard.json.rpc.protocol.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Date: 07.06.14
 * Time: 15:16
 *
 * @author Artem Prigoda
 */
public class ErrorMessage {

    private final int code;
    private final String message;

    public ErrorMessage(@JsonProperty int code, @JsonProperty String message) {
        this.code = code;
        this.message = message;
    }
}
