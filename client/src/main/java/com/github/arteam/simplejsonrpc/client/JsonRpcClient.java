package com.github.arteam.simplejsonrpc.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.arteam.simplejsonrpc.client.builder.BatchRequestBuilder;
import com.github.arteam.simplejsonrpc.client.builder.NotificationRequestBuilder;
import com.github.arteam.simplejsonrpc.client.builder.ObjectApiBuilder;
import com.github.arteam.simplejsonrpc.client.builder.RequestBuilder;
import com.github.arteam.simplejsonrpc.client.generator.IdGenerator;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Proxy;
import java.util.Optional;

import static java.util.Optional.empty;

/**
 * Date: 8/9/14
 * Time: 8:58 PM
 * <p> JSON-RPC client. Represents a factory for a fluent client API {@link RequestBuilder}.
 * It's parametrized by {@link Transport} and Jackson {@link ObjectMapper}</p>
 */
public class JsonRpcClient {

    public class ServiceProxyBuilder<T> {

        // interface metadata
        final Class<T> service;
        // service name
        String serviceName;
        // custom id generator
        Optional<IdGenerator<?>> idGenerator = empty();
        // custom type of request params
        Optional<ParamsType> paramsType = empty();

        public ServiceProxyBuilder(Class<T> service) {
            this.service = service;
            this.serviceName = service.getCanonicalName();
        }

        public ServiceProxyBuilder<T> serviceName(@NotNull String serviceName) {
            this.serviceName = serviceName;
            return this;
        }

        public ServiceProxyBuilder<T> idGenerator(@NotNull IdGenerator<?> idGenerator) {
            this.idGenerator  = Optional.of(idGenerator);
            return this;
        }

        public ServiceProxyBuilder<T> paramsType(@NotNull ParamsType paramsType) {
            this.paramsType  = Optional.of(paramsType);
            return this;
        }

        @SuppressWarnings("unchecked")
        public T build() {

            final ObjectApiBuilder apiBuilder =
                    new ObjectApiBuilder(   service,
                                            serviceName,
                                            transport,
                                            mapper,
                                            paramsType,
                                            idGenerator);

            return (T) Proxy.newProxyInstance(  getClass().getClassLoader(),
                                                new Class[]{service},
                                                apiBuilder );

        }

    }

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

    /**
     * Creates a new proxy for accessing a remote JSON-RPC service through an interface
     *
     * @param clazz interface metadata
     * @param <T>   interface type
     * @return a new proxy
     */
    @NotNull
    public <T> ServiceProxyBuilder<T> onDemand(@NotNull Class<T> clazz) {
        return new ServiceProxyBuilder<T>(clazz);
    }

}
