package com.fit.se.application.qr.generate;

import lombok.Builder;

@Builder
public record QRChallengeResult(
        String challengeId,
        String content,
        long expiredIn
) {
}
