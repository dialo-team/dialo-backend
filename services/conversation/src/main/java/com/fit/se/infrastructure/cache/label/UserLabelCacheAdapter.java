package com.fit.se.infrastructure.cache.label;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class UserLabelCacheAdapter {

    @Cacheable(cacheNames = "label", key = "#key")
    public Object get(String key, Object fallback) {
        return fallback;
    }
}
