package com.fit.se.infrastructure.security.token;

import com.fit.se.infrastructure.cache.CacheStore;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TokenStore {
    private final CacheStore cache;

    private final String REFRESH_TOKEN_KEY_PREFIX  = "token:refresh:";
    private static final String USER_REFRESH_TOKEN_SET_PREFIX = "token:user:";

    @Value("${app.security.jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    public void store(String token, String subject, String jti) {
        String tokenKey = buildKey(subject, jti);
        String userTokenSetKey = buildUserTokenSetKey(subject);

        System.out.println("token: " + hash(token));
        System.out.println("subject: " + subject);
        System.out.println("jti: " + jti);

        cache.putHash(tokenKey, Map.of(
                "token", hash(token),
                "subject", subject,
                "jti", jti
        ));
        cache.expire(tokenKey, refreshTokenExpiration);
        cache.addToSet(userTokenSetKey, jti);
    }

    public boolean exists(String subject, String jti) {
        return cache.exists(buildKey(subject, jti));
    }

    public boolean matches(String token, String subject, String jti) {
        String key = buildKey(subject, jti);
        Optional<Object> storedHash = cache.getHashField(key, "token");
        return storedHash
                .map(value -> value.equals(hash(token)))
                .orElse(false);
    }

    public void revoke(String subject, String jti) {
        cache.delete(buildKey(subject, jti));
    }

    private String buildKey(String subject, String jti) {
        return REFRESH_TOKEN_KEY_PREFIX  + subject + ":" + jti;
    }

    private String buildUserTokenSetKey(String userId) {
        return USER_REFRESH_TOKEN_SET_PREFIX + userId + ":refresh-tokens";
    }

    public String hash(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot hash token", e);
        }
    }
}
