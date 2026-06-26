package com.fit.se.common.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class RedisAdapter implements CacheStore {
    private final StringRedisTemplate template;

    @Override
    public void put(String key, String value) {
        template.opsForValue().set(key, value);
    }

    @Override
    public void put(String key, String value, long ttlMillis) {
        template.opsForValue().set(key, value, Duration.ofMillis(ttlMillis));
    }

    @Override
    public Optional<String> get(String key) {
        return Optional.ofNullable(template.opsForValue().get(key));
    }

    @Override
    public void delete(String key) {
        template.delete(key);
    }

    @Override
    public boolean exists(String key) {
        return Boolean.TRUE.equals(template.hasKey(key));
    }

    @Override
    public void expire(String key, long ttlMillis) {
        template.expire(key, Duration.ofMillis(ttlMillis));
    }

    @Override
    public void putHash(String key, Map<String, String> values) {
        template.opsForHash().putAll(key, values);
    }

    @Override
    public Map<Object, Object> getHash(String key) {
        return template.opsForHash().entries(key);
    }

    @Override
    public Optional<Object> getHashField(String key, String field) {
        return Optional.ofNullable(template.opsForHash().get(key, field));
    }

    @Override
    public void addToSet(String key, String value) {
        template.opsForSet().add(key, value);
    }

    @Override
    public void removeFromSet(String key, String value) {
        template.opsForSet().remove(key, value);
    }

    @Override
    public Set<String> getSetMembers(String key) {
        Set<String> members = template.opsForSet().members(key);
        return members == null ? Set.of() : members;
    }
}
