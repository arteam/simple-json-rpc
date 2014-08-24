package com.github.arteam.simplejsonrpc.client;

import com.github.arteam.simplejsonrpc.client.generator.IdGenerator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Date: 24.08.14
 * Time: 18:14
 *
 * @author Artem Prigoda
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonRpcId {

    Class<? extends IdGenerator<?>> value();
}
