package com.github.arteam.simplejsonrpc.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;

/**
 * Date: 8/9/14
 * Time: 8:58 PM
 *
 * @author Artem Prigoda
 */
public class JsonRpcClient {

    @NotNull
    private Transport transport;

    @NotNull
    private ObjectMapper mapper = new ObjectMapper();

    public JsonRpcClient(@NotNull Transport transport) {
        this.transport = transport;
    }

    public JsonRpcClient(@NotNull Transport transport, @NotNull ObjectMapper mapper) {
        this.transport = transport;
        this.mapper = mapper;
    }

    public RequestBuilder<?> createRequest() {
        return new RequestBuilder(transport, mapper);
    }

}
