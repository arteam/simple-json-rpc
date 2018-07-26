package com.github.arteam.simplejsonrpc.client;

import java.lang.annotation.*;

/**
 * Date: 11/4/14
 * Time: 10:45 PM
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
public @interface JsonRpcParams {

    ParamsType value() default ParamsType.MAP;
}
