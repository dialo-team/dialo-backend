package com.fit.se.infrastructure.cache.join;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class JoinTokenCacheAdapter {

    @Cacheable(cacheNames = "join", key = "#key")
    public Object get(String key, Object fallback) {
        return fallback;
    }
}
