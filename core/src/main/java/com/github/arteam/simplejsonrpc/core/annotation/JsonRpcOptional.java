package com.github.arteam.simplejsonrpc.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Date: 6/15/14
 * Time: 1:49 AM
 * <p/>
 * Annotation for marking a parameter as an optional.
 * <p/>
 * It means a client isn't forced to pass this parameter to the method. If the client doesn't provide it,
 * {@code null} value is used for complex types and an appropriate default value for primitives.
 *
 * @author Artem Prigoda
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonRpcOptional {
}
