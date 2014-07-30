package com.github.arteam.dropwizard.json.rpc.protocol.domain;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Date: 07.06.14
 * Time: 13:02
 * Mark for method
 *
 * @author Artem Prigoda
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonRpcMethod {

    String value() default "";
}
