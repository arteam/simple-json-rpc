package com.github.arteam.simplejsonrpc.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Date: 7/31/14
 * Time: 6:03 PM
 * Annotation for marking an exception as a JSON-RPC error
 *
 * @author Artem Prigoda
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonRpcError {

    /**
     * JSON-RPC error code
     *
     * @return error code
     */
    int code() default 0;

    /**
     * JSON-RPC error message.
     * If empty then the exception message will be used
     *
     * @return error message
     */
    String message() default "";
}
