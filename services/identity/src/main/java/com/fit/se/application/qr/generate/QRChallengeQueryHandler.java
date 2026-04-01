package com.fit.se.application.qr.generate;

import com.fit.se.infrastructure.cache.CacheStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QRChallengeQueryHandler {
    private final CacheStore cache;

    private static final long QR_CHALLENGE_EXPIRE_MILLIS = 2 * 60 * 1000;

    public QRChallengeResult execute(QRChallengeQuery query) {
        String challengeId = UUID.randomUUID().toString();
        String challengeKey = "auth:qr:challenge:" + challengeId;

        Instant createdAt = Instant.now();
        Instant expiredAt = createdAt.plusMillis(QR_CHALLENGE_EXPIRE_MILLIS);

        cache.putHash(challengeKey, Map.of(
                "status","PENDING",
                "deviceIp", query.ipAddress(),
                "devicName", query.agent(),
                "createdAt", Instant.now().toString(),
                "expiredAt", expiredAt.toString(),
                "approveBy", ""
        ));
        cache.expire(challengeKey, QR_CHALLENGE_EXPIRE_MILLIS);

        String content = challengeId;

        return QRChallengeResult.builder()
                .challengeId(challengeId)
                .content(content)
                .expiredIn(QR_CHALLENGE_EXPIRE_MILLIS)
                .build();
    }
}
