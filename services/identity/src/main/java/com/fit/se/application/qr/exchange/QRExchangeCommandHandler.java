package com.fit.se.application.qr.exchange;

import com.fit.se.infrastructure.cache.CacheStore;
import com.fit.se.infrastructure.security.token.TokenProvider;
import com.fit.se.infrastructure.security.token.TokenPurpose;
import com.fit.se.infrastructure.security.token.TokenStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class QRExchangeCommandHandler {
    private final CacheStore cache;
    private final TokenProvider tokenProvider;
    private final TokenStore tokenStore;

    public QRExchangeResult execute(QRExchangeCommand cmd) {
        String challengeKey = "auth:qr:challenge:" + cmd.challengeId();

        Map<Object, Object> challenge = cache.getHash(challengeKey);
        if (challenge == null || challenge.isEmpty()) {
            throw new RuntimeException("QR challenge not found");
        }

        String status = String.valueOf(challenge.get("status"));
        if (!"APPROVED".equals(status)) {
            throw new RuntimeException("QR challenge has not been approved");
        }

        String userId = String.valueOf(challenge.get("approvedBy"));
        if (userId == null || userId.isBlank()) {
            throw new RuntimeException("QR challenge approved user not found");
        }

        String accessToken = tokenProvider.generate(TokenPurpose.ACCESS, userId);
        String refreshToken = tokenProvider.generate(TokenPurpose.REFRESH, userId);

        tokenStore.store(refreshToken, userId, tokenProvider.extractJti(refreshToken));

        cache.putHash(challengeKey, Map.of(
                "status", "EXCHANGED",
                "exchangedAt", Instant.now().toString()
        ));

        cache.expire(challengeKey, 30_000);

        return QRExchangeResult.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .build();
    }
}
