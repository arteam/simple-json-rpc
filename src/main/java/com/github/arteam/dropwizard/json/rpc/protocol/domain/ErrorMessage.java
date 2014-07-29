package com.github.arteam.dropwizard.json.rpc.protocol.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

/**
 * Date: 07.06.14
 * Time: 15:16
 *
 * @author Artem Prigoda
 */
public class ErrorMessage {

    private final int code;

    @NotNull
    private final String message;

    public ErrorMessage(@JsonProperty int code, @JsonProperty @NotNull String message) {
        this.code = code;
        this.message = message;
    }
}
