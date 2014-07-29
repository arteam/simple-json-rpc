package com.github.arteam.dropwizard.json.rpc.protocol.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ValueNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Date: 07.06.14
 * Time: 12:34
 *
 * @author Artem Prigoda
 */
public class Response {

    private static final String VERSION = "2.0";

    @NotNull
    @JsonProperty("jsonrpc")
    private final String jsonrpc = VERSION;

    @Nullable
    @JsonProperty("id")
    private final ValueNode id;

    public Response(@Nullable ValueNode id) {
        this.id = id;
    }
}
