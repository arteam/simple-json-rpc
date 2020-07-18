package com.github.arteam.simplejsonrpc.server.metadata;

import java.util.Optional;

@FunctionalInterface
public interface ErrorDataResolver {

    Optional<Object> resolveData(Throwable throwable) throws Exception;
}
