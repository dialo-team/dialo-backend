package com.fit.se.application.token.generate;

import lombok.Builder;

@Builder
public record GenerateTokenResult(
        String accessToken,
        String refreshToken,
        String tokenType,
        String sessId
) {}
