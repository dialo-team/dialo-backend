package com.fit.se.application.token.refresh;

import com.fit.se.api.exception.tokens.RefreshTokenNotFoundException;
import com.fit.se.infrastructure.token.JwtService;
import com.fit.se.infrastructure.redis.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenQueryHandler {
    private final JwtService jwtService;
    private final RedisService redisService;

    public RefreshTokenResult execute(RefreshTokenQuery req) {
        String phone = jwtService.extractUserName(req.refreshToken());
        String refreshToken = redisService.get(phone).orElseThrow(RefreshTokenNotFoundException::new);
        redisService.delete(phone);

        final String newAccessToken = jwtService.generateAccessToken(refreshToken);
        final String tokenType = "Bearer";
        return RefreshTokenResult.builder()
                .accessToken(newAccessToken)
                .refreshToken(req.refreshToken())
                .tokenType(tokenType)
                .build();
    }
}
