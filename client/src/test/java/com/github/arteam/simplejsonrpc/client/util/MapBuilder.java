package com.github.arteam.simplejsonrpc.client.util;

import java.util.HashMap;
import java.util.Map;

public class MapBuilder<K, V> {

    private final Map<K, V> map = new HashMap<>();

    public MapBuilder<K, V> put(K k, V v) {
        map.put(k, v);
        return this;
    }

    public Map<K, V> build() {
        return map;
    }

}
