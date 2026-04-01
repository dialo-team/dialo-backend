package com.fit.se.application.token.refresh;

import com.fit.se.api.exception.tokens.RefreshTokenNotFoundException;
import com.fit.se.infrastructure.cache.CacheStore;
import com.fit.se.infrastructure.security.token.TokenProvider;
import com.fit.se.infrastructure.security.token.TokenPurpose;
import com.fit.se.infrastructure.security.token.TokenStore;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenQueryHandler {
    private final TokenProvider tokenProvider;
    private final CacheStore cache;
    private final TokenStore tokenStore;

    public RefreshTokenResult execute(RefreshTokenQuery query) {
        Claims claims = tokenProvider.extractClaims(query.refreshToken());
        String subject = claims.getSubject();
        String jti = claims.getId();

        String deviceKey = "token:refresh:" + subject + ":" + jti;

        String storedRefreshToken = cache.getHashField(deviceKey, "token")
                .map(Object::toString)
                .orElseThrow(RefreshTokenNotFoundException::new);
        if (!storedRefreshToken.equals(tokenStore.hash(query.refreshToken()))) {
            System.out.println("store: " + storedRefreshToken);
            System.out.println("refre: " + query.refreshToken());

            throw new RefreshTokenNotFoundException();
        }

        System.out.println("store: " + storedRefreshToken);
        System.out.println("refre: " + tokenStore.hash(query.refreshToken()));

        cache.delete(deviceKey);

        System.out.println("device-key: " + deviceKey);

        final String newAccessToken = tokenProvider.generate(TokenPurpose.ACCESS, subject);
        final String tokenType = "Bearer";
        return RefreshTokenResult.builder()
                .accessToken(newAccessToken)
                .refreshToken(query.refreshToken())
                .tokenType(tokenType)
                .build();
    }
}
