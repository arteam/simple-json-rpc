package com.github.arteam.json.rpc.simple.annotation;

import java.lang.annotation.*;

/**
 * Date: 8/2/14
 * Time: 6:10 PM
 * Annotation for marking a service as a JSON-RPC service
 *
 * @author Artem Prigoda
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface JsonRpcService {
}
