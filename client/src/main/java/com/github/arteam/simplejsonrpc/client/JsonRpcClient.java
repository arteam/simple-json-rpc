package com.github.arteam.simplejsonrpc.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.arteam.simplejsonrpc.client.builder.BatchRequestBuilder;
import com.github.arteam.simplejsonrpc.client.builder.NotificationRequestBuilder;
import com.github.arteam.simplejsonrpc.client.builder.ObjectApiBuilder;
import com.github.arteam.simplejsonrpc.client.builder.RequestBuilder;
import com.github.arteam.simplejsonrpc.client.generator.IdGenerator;

import java.lang.reflect.Proxy;

/**
 * Date: 8/9/14
 * Time: 8:58 PM
 * <p>
 * JSON-RPC client. Represents a factory for a fluent client API {@link RequestBuilder}.
 * It's parametrized by {@link Transport} and Jackson {@link ObjectMapper}
 */
public class JsonRpcClient {

    /**
     * Transport for performing JSON-RPC requests and returning responses
     */
    private final Transport transport;

    /**
     * JSON mapper for conversion between JSON and Java types
     */
    private final ObjectMapper mapper;

    /**
     * Constructs a new JSON-RPC client with a specified transport
     *
     * @param transport transport implementation
     */
    public JsonRpcClient(Transport transport) {
        this(transport, new ObjectMapper());
    }

    /**
     * Constructs a new JSON-RPC client with a specified transport and user-definder JSON mapper
     *
     * @param transport transport implementation
     * @param mapper    JSON mapper
     */
    public JsonRpcClient(Transport transport, ObjectMapper mapper) {
        this.transport = transport;
        this.mapper = mapper;
    }

    /**
     * Creates a builder of a JSON-RPC request in initial state
     *
     * @return request builder
     */
    public RequestBuilder<Object> createRequest() {
        return new RequestBuilder<>(transport, mapper);
    }

    /**
     * Creates a builder of a JSON-RPC notification request in initial state
     *
     * @return notification request builder
     */
    public NotificationRequestBuilder createNotification() {
        return new NotificationRequestBuilder(transport, mapper);
    }

    /**
     * Creates a builder of a JSON-RPC batch request in initial state
     *
     * @return batch request builder
     */
    public BatchRequestBuilder<?, ?> createBatchRequest() {
        return new BatchRequestBuilder<>(transport, mapper);
    }

    /**
     * Creates a new proxy for accessing a remote JSON-RPC service through an interface
     *
     * @param clazz interface metadata
     * @param <T>   interface type
     * @return a new proxy
     */
    @SuppressWarnings("unchecked")
    public <T> T onDemand(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{clazz},
                new ObjectApiBuilder(clazz, transport, mapper, null, null));
    }

    /**
     * Creates a new proxy for accessing a remote JSON-RPC service through an interface
     * with a custom id generator that overrides the interface generator.
     *
     * @param clazz       interface metadata
     * @param idGenerator custom id generator
     * @param <T>         interface type
     * @return a new proxy
     */
    @SuppressWarnings("unchecked")
    public <T> T onDemand(Class<T> clazz, IdGenerator<?> idGenerator) {
        return (T) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{clazz},
                new ObjectApiBuilder(clazz, transport, mapper, null, idGenerator));
    }

    /**
     * Creates a new proxy for accessing a remote JSON-RPC service through an interface
     * with a custom type of request params.
     * It applies for all methods and overrides interface and method level settings.
     *
     * @param clazz      interface metadata
     * @param paramsType custom type of request params
     * @param <T>        interface type
     * @return a new proxy
     */
    @SuppressWarnings("unchecked")
    public <T> T onDemand(Class<T> clazz, ParamsType paramsType) {
        return (T) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{clazz},
                new ObjectApiBuilder(clazz, transport, mapper, paramsType, null));
    }

    /**
     * Creates a new proxy for accessing a remote JSON-RPC service through an interface
     * with a custom id generator and custom type of request params.
     * The generator overrides the interface generator.
     * The type applies for all methods and overrides interface and method level settings.
     *
     * @param clazz       interface metadata
     * @param idGenerator custom id generator
     * @param paramsType  custom type of request params
     * @param <T>         interface type
     * @return a new proxy
     */
    @SuppressWarnings("unchecked")
    public <T> T onDemand(Class<T> clazz, ParamsType paramsType, IdGenerator<?> idGenerator) {
        return (T) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{clazz},
                new ObjectApiBuilder(clazz, transport, mapper, paramsType, idGenerator));
    }

}
