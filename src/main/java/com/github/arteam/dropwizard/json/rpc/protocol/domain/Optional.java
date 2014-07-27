package com.github.arteam.dropwizard.json.rpc.protocol.domain;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Date: 6/15/14
 * Time: 1:49 AM
 *
 * @author Artem Prigoda
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Optional {
}
