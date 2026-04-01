package com.fit.se.infrastructure.cache;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface CacheStore {
    void put(String key, String value);
    void put(String key, String value, long ttlMillis);
    Optional<String> get(String key);

    void delete(String key);
    boolean exists(String key);
    void expire(String key, long ttlMillis);

    void putHash(String key, Map<String, String> values);
    Map<Object, Object> getHash(String key);
    Optional<Object> getHashField(String key, String field);

    void addToSet(String key, String value);
    void removeFromSet(String key, String value);
    Set<String> getSetMembers(String key);
}
