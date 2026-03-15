package com.fit.se.application.token.revoke;

import com.fit.se.api.exception.tokens.RefreshTokenNotFoundException;
import com.fit.se.infrastructure.token.JwtService;
import com.fit.se.infrastructure.redis.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RevokeTokenHandler {
    private final JwtService jwtService;
    private final RedisService redisService;

    public void execute(RevokeQuery req) {
        String userId = jwtService.extractUserName(req.refreshToken());
        String storedRefreshToken = redisService.get(userId)
                .orElseThrow(RefreshTokenNotFoundException::new);
        if (!storedRefreshToken.equals(req.refreshToken())) {
            throw new RuntimeException("Invalid refresh token");
        }
        redisService.delete(userId);
    }
}
