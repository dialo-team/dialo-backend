package com.fit.se.infrastructure.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RedisServiceImpl implements RedisService {
    private final StringRedisTemplate redisTemplate;

    @Value("${spring.data.redis.timeout}")
    private long timeOut;

    public void set(String key, String value) {
        redisTemplate.opsForValue()
                .set(key, value, Duration.ofMillis(timeOut));
    }

    public void setEx(String key, String value, long milliSeconds) {
        redisTemplate.opsForValue()
                .set(key, value, Duration.ofMillis(milliSeconds));
    }

    public Optional<String> get(String key) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(key));
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }

    public void addToSet(String key, String value) {
        redisTemplate.opsForSet().add(key, value);
    }

    public void removeFromSet(String key, String value) {
        redisTemplate.opsForSet().remove(key, value);
    }

    public void putHash(String key, Map<String, String> values) {
        redisTemplate.opsForHash().putAll(key, values);
    }

    public Optional<Object> getHashField(String key, String field) {
        return Optional.ofNullable(redisTemplate.opsForHash().get(key, field));
    }

    public Map<Object, Object> getHash(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    public void expire(String key, long millis) {
        redisTemplate.expire(key, Duration.ofMillis(millis));
    }

    @Override
    public Set<String> getSetMembers(String key) {
        return redisTemplate.opsForSet().members(key);
    }
}

