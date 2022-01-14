package com.github.arteam.simplejsonrpc.client.builder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.arteam.simplejsonrpc.client.Transport;

/**
 * Date: 8/17/14
 * Time: 11:09 PM
 * <p>Type-safe builder of JSON-RPC notification requests.</p>
 * <p>It uses underlying {@link RequestBuilder} to build a request,  but not permits setting a request id.
 * Also it doesn't expect any response from the server, so there is no response parsing.</p>
 */
public class NotificationRequestBuilder {

    /**
     * Delegated request builder
     */
    private final RequestBuilder<Void> requestBuilder;

    /**
     * Creates a new notification request builder
     *
     * @param transport transport for request performing
     * @param mapper    mapper for JSON processing
     */
    public NotificationRequestBuilder(Transport transport, ObjectMapper mapper) {
        requestBuilder = new RequestBuilder<>(transport, mapper);
    }

    /**
     * Creates a new notification request builder as a chain of builders
     *
     * @param requestBuilder a new notification request builder
     */
    private NotificationRequestBuilder(RequestBuilder<Void> requestBuilder) {
        this.requestBuilder = requestBuilder;
    }

    /**
     * Sets a request method
     *
     * @param method a request method
     * @return new builder
     */
    public NotificationRequestBuilder method(String method) {
        return new NotificationRequestBuilder(requestBuilder.method(method));
    }

    /**
     * Adds a new parameter to current request parameters.
     *
     * @param name  parameter name
     * @param value parameter value
     * @return new builder
     */
    public NotificationRequestBuilder param(String name, Object value) {
        return new NotificationRequestBuilder(requestBuilder.param(name, value));
    }

    /**
     * Sets request parameters to request parameters.
     * Parameters are interpreted according to its positions.
     *
     * @param values array of parameters
     * @return new builder
     */
    public NotificationRequestBuilder params(Object... values) {
        return new NotificationRequestBuilder(requestBuilder.params(values));
    }

    /**
     * Execute a request through {@link Transport}
     */
    public void execute() {
        requestBuilder.executeRequest();
    }

}
