package com.fit.se.infrastructure.cache.key;

public final class CacheKeyBuilder {

    private CacheKeyBuilder() {
    }

    public static String conversation(String suffix) {
        return "conversation:" + suffix;
    }

    public static String label(String suffix) {
        return "label:" + suffix;
    }

    public static String join(String suffix) {
        return "join:" + suffix;
    }
}
