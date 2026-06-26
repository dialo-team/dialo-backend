package com.fit.se.auth.application.token.refresh;

import com.fit.se.common.exception.tokens.RefreshTokenInvalidException;
import com.fit.se.common.exception.tokens.RefreshTokenNotFoundException;
import com.fit.se.common.security.token.TokenProvider;
import com.fit.se.common.security.token.TokenPurpose;
import com.fit.se.common.security.token.TokenStore;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenQueryHandler {
    private final TokenProvider tokenProvider;
    private final TokenStore tokenStore;

    public RefreshTokenResult execute(RefreshTokenQuery query) {
        Claims claims = extractRefreshClaims(query.refreshToken());
        String subject = claims.getSubject();
        String jti = claims.getId();

        if (!tokenStore.exists(subject, jti)) {
            throw new RefreshTokenNotFoundException();
        }

        if (!tokenStore.matches(query.refreshToken(), subject, jti)) {
            throw new RefreshTokenInvalidException();
        }

        String newAccessToken = tokenProvider.generate(TokenPurpose.ACCESS, subject);
        return RefreshTokenResult.builder()
                .accessToken(newAccessToken)
                .refreshToken(query.refreshToken())
                .tokenType("Bearer")
                .build();
    }

    private Claims extractRefreshClaims(String refreshToken) {
        try {
            Claims claims = tokenProvider.extractClaims(refreshToken);
            if (tokenProvider.extractPurpose(refreshToken) != TokenPurpose.REFRESH) {
                throw new RefreshTokenInvalidException();
            }
            return claims;
        } catch (RefreshTokenInvalidException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RefreshTokenInvalidException();
        }
    }
}

