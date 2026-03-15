package com.fit.se.infrastructure.redis;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface RedisService {
    public void set(String key, String value);
    public void setEx(String key, String value, long milliSeconds);
    public Optional<String> get(String key);
    public void delete(String key);
    public void addToSet(String key, String value);
    public void removeFromSet(String key, String value);
    public void putHash(String key, Map<String, String> values);
    public Optional<Object> getHashField(String key, String field);
    public void expire(String key, long millis);
    public Set<String> getSetMembers(String key);
    public Map<Object, Object> getHash(String key);
}
