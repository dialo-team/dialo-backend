package com.fit.se.infrastructure.cache.conversation;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class ConversationCacheAdapter {

    @Cacheable(cacheNames = "conversation", key = "#key")
    public Object get(String key, Object fallback) {
        return fallback;
    }
}
