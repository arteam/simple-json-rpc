package com.github.arteam.simplejsonrpc.server.serviceresolver;

import com.github.arteam.simplejsonrpc.server.JsonRpcServer;
import java.util.HashMap;
import java.util.Map;

/**
 * JSON-RPC service resolver for multiple services encoded in
 * JSON request method name.
 * Expect naming "service.method" dot separated string with only one
 * dot service name depth.
 */
public class DefaultServiceResolver implements JsonRpcServer.ServiceResolver {

    private final Map<String, Object> services = new HashMap<>();
    
    /**
     * Return resolved service object to given service address.
     * This implementation works on simple string mapping.
     * @param serviceAddress extracted from request method.
     * @return service object, or null if service is not known
     */
    @Override
    public Object getService(String serviceAddress) {
        return services.get(serviceAddress);
    }

    /**
     * Register a service object with given serviceAddress
     * @param serviceAddress for registering the service
     * @param service object to be registered
     * @return this for fluent usage
     */
    public DefaultServiceResolver add(String serviceAddress, Object service) {
        services.put(serviceAddress, service);
        return this;
    }
    
    @Override
    public String extractMethod(String method) {
        //expect "service.method", dot separated string with only one dot.
        int lastIndexOfDot = method.lastIndexOf('.');
        if (-1 != lastIndexOfDot) {
            return method.substring(lastIndexOfDot + 1);
        } else {
            return method;
        }
    }

    @Override
    public String extractServiceAddress(String method) {
        //expect "service.method", dot separated string with only one dot.
        int lastIndexOfDot = method.lastIndexOf('.');
        if (-1 != lastIndexOfDot) {
            return method.substring(0, lastIndexOfDot);
        } else {
            return null;
        }
    }
    
}
