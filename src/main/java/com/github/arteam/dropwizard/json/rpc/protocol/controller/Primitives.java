package com.github.arteam.dropwizard.json.rpc.protocol.controller;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Date: 7/29/14
 * Time: 11:35 PM
 * Primitives utils
 *
 * @author Artem Prigoda
 */
class Primitives {

    @SuppressWarnings("serial")
    private static final Map<Class<?>, Object> DEFAULTS = Collections.unmodifiableMap(
            new HashMap<Class<?>, Object>() {{
                put(boolean.class, false);
                put(char.class, '\0');
                put(byte.class, (byte) 0);
                put(short.class, (short) 0);
                put(int.class, 0);
                put(long.class, 0L);
                put(float.class, 0f);
                put(double.class, 0d);
            }});

    private Primitives() {
    }

    @Nullable
    public static Object getDefaultValue(@NotNull Class<?> clazz) {
        return DEFAULTS.get(clazz);
    }
}
