package com.fit.se.application.qr.approve;

import com.fit.se.infrastructure.cache.CacheStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class QRApproveCommandHandler {
    private final CacheStore cache;

    public void execute(QRApproveCommand cmd) {
        String challengeKey = "auth:qr:challenge:" + cmd.challengeId();

        Map<Object, Object> challenge = cache.getHash(challengeKey);
        if (challenge == null || challenge.isEmpty()) {
            throw new RuntimeException("QR challenge not found");
        }

        String status = String.valueOf(challenge.get("status"));
        if (!"PENDING".equals(status)) {
            throw new RuntimeException("QR challenge is not pending");
        }

        cache.putHash(challengeKey, Map.of(
                "status", "APPROVED",
                "approvedBy", cmd.userId(),
                "approvedAt", Instant.now().toString()
        ));
    }
}
