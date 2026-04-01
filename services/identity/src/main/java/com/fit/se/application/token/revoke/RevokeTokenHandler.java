package com.fit.se.application.token.revoke;

import com.fit.se.api.exception.tokens.RefreshTokenNotFoundException;
import com.fit.se.infrastructure.cache.CacheStore;
import com.fit.se.infrastructure.security.token.TokenProvider;
import com.fit.se.infrastructure.security.token.TokenStore;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class RevokeTokenHandler {
    private final CacheStore cache;
    private final TokenProvider tokenProvider;
    private final TokenStore tokenStore;

    public void execute(RevokeCommand cmd) {
        Claims claims = tokenProvider.extractClaims(cmd.refreshToken());
        String subject = claims.getSubject();
        String jti = claims.getId();

        System.out.println("subject: " + subject);
        System.out.println("jti: " + jti);

        String deviceKey = "token:refresh:" + cmd.subject() + ":" + jti;
        String storedHash = cache.getHashField(deviceKey, "token")
                .map(Object::toString)
                .orElseThrow(RefreshTokenNotFoundException::new);
        String presentedHash = tokenStore.hash(cmd.refreshToken());

        System.out.println("result: " + storedHash.equals(presentedHash));

        if (!storedHash.equals(presentedHash)) {
            throw new RuntimeException("Invalid refresh token");
        }
        cache.delete(deviceKey);
    }

    public void execute(RevokeAllCommand cmd) {
        Claims claims = tokenProvider.extractClaims(cmd.refreshToken());
        String subject = claims.getSubject();

        String deviceSetKey = "token:user:" + subject + ":refresh-tokens";

        Set<String> jtis = cache.getSetMembers(deviceSetKey);

        if (jtis == null || jtis.isEmpty()) {
            throw new RefreshTokenNotFoundException();
        }

        String presentedHash = tokenStore.hash(cmd.refreshToken());
        boolean matched = false;

        for (String jti : jtis) {
            String deviceKey = "token:refresh:" + subject + ":" + jti;

            String storedHash = cache.getHashField(deviceKey, "token")
                    .map(Object::toString)
                    .orElse(null);

            if (storedHash != null && storedHash.equals(presentedHash)) {
                matched = true;
            }

            cache.delete(deviceKey);
        }

        cache.delete(deviceSetKey);

        if (!matched) {
            throw new RuntimeException("Invalid refresh token");
        }
    }
}
