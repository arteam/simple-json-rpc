package com.github.arteam.simplejsonrpc.server;

import com.github.arteam.simplejsonrpc.server.metadata.ClassMetadata;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

public class JsonRpcServerRegistry {
    private static final Logger log = LoggerFactory.getLogger(JsonRpcServerRegistry.class);

    private java.util.Map<String,Object> registry = new HashMap<>();

    @NotNull
    final JsonRpcServer server;

    private final Optional<Object> defaultService;

    /**
     *
     * @param server
     */
    public JsonRpcServerRegistry(@NotNull JsonRpcServer server ) {
        this.server = server;
        this.defaultService = empty();
    }
    /**
     *
     * @param server
     */
    public JsonRpcServerRegistry(@NotNull JsonRpcServer server, @NotNull Object defaultService ) {
        this.server = server;
        this.defaultService = Optional.of(defaultService);
    }

    /**
     *
     * @return
     */
    @NotNull
    public JsonRpcServer getServer() {
        return server;
    }

    /**
     *
     * @param service
     * @return service name
     */
    public String bind( @NotNull Object service ) {

        final ClassMetadata serviceMetadata = server.getServiceMetadata(service);

        final String serviceName = serviceMetadata.getServiceName();

        Object prevValue = registry.putIfAbsent( serviceName, service );
        if( prevValue != service ) {
            log.warn( "service with name '%s' was already bound. operation ignored!", serviceName );
        }
        return serviceName;
    }

    /**
     *
     * @return
     */
    @NotNull
    public java.util.Set<String> getServiceNames() {
        return registry.keySet();
    }

    /**
     *
     * @param serviceName
     * @return
     */
    public Optional<Object> unbind( @NotNull String serviceName ) {
        return ofNullable(registry.remove( serviceName ));
    }

    /**
     *
     * @param serviceName
     * @return
     */
    public Optional<Object> lookup( @NotNull  String serviceName ) {
        final Object service = registry.get(serviceName);
        return (service!=null) ? Optional.of(service) : defaultService ;
    }

}
