package com.fit.se.auth.application.signin;

import lombok.Builder;

@Builder
public record QRChallengeResult(
        String challengeId,
        String content,
        long expiredIn
) {
}

