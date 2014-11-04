package com.github.arteam.simplejsonrpc.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.arteam.simplejsonrpc.client.builder.BatchRequestBuilder;
import com.github.arteam.simplejsonrpc.client.builder.NotificationRequestBuilder;
import com.github.arteam.simplejsonrpc.client.builder.RequestBuilder;
import com.github.arteam.simplejsonrpc.client.generator.IdGenerator;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Proxy;

/**
 * Date: 8/9/14
 * Time: 8:58 PM
 * <p/>
 * JSON-RPC client. Represents a factory for a fluent client API {@link com.github.arteam.simplejsonrpc.client.builder.RequestBuilder}.
 * It's parametrized by {@link Transport} and Jackson {@link ObjectMapper}
 *
 * @author Artem Prigoda
 */
public class JsonRpcClient {

    /**
     * Transport for performing JSON-RPC requests and returning responses
     */
    @NotNull
    private Transport transport;

    /**
     * JSON mapper for conversion between JSON and Java types
     */
    @NotNull
    private ObjectMapper mapper;

    /**
     * Constructs a new JSON-RPC client with a specified transport
     *
     * @param transport transport implementation
     */
    public JsonRpcClient(@NotNull Transport transport) {
        this(transport, new ObjectMapper());
    }

    /**
     * Constructs a new JSON-RPC client with a specified transport and user-definder JSON mapper
     *
     * @param transport transport implementation
     * @param mapper    JSON mapper
     */
    public JsonRpcClient(@NotNull Transport transport, @NotNull ObjectMapper mapper) {
        this.transport = transport;
        this.mapper = mapper;
    }

    /**
     * Creates a builder of a JSON-RPC request in initial state
     *
     * @return request builder
     */
    @NotNull
    public RequestBuilder<Object> createRequest() {
        return new RequestBuilder<Object>(transport, mapper);
    }

    /**
     * Creates a builder of a JSON-RPC notification request in initial state
     *
     * @return notification request builder
     */
    @NotNull
    public NotificationRequestBuilder createNotification() {
        return new NotificationRequestBuilder(transport, mapper);
    }

    /**
     * Creates a builder of a JSON-RPC batch request in initial state
     *
     * @return batch request builder
     */
    @NotNull
    public BatchRequestBuilder<?, ?> createBatchRequest() {
        return new BatchRequestBuilder<Object, Object>(transport, mapper);
    }


    @SuppressWarnings("unchecked")
    public <T> T onDemand(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{clazz},
                new ObjectAPIProxyBuilder(transport, mapper, null));
    }

    @SuppressWarnings("unchecked")
    public <T> T onDemand(Class<T> clazz, IdGenerator<?> idGenerator) {
        return (T) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{clazz},
                new ObjectAPIProxyBuilder(transport, mapper, idGenerator));
    }

}
