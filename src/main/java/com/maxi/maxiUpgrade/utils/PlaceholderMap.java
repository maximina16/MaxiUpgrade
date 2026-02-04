package com.maxi.maxiUpgrade.utils;

import java.util.HashMap;
import java.util.Map;

public final class PlaceholderMap {

    private final Map<String, String> map = new HashMap<>();

    public PlaceholderMap add(String key, Object value) {
        map.put(key, value == null ? "" : String.valueOf(value));
        return this;
    }

    public Map<String, String> build() {
        return map;
    }
}
