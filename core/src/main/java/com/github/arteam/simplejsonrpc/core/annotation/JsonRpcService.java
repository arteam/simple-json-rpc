package com.github.arteam.simplejsonrpc.core.annotation;

import java.lang.annotation.*;

/**
 * Date: 8/2/14
 * Time: 6:10 PM
 * Annotation for marking a service as a JSON-RPC service
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface JsonRpcService {
    /**
     * RPC service name. <b>if it is empty, it will be set the canonical name of class</b>.
     * It is used to bind and retrieve service
     *
     * @return service name
     */
    String value() default "";

}
