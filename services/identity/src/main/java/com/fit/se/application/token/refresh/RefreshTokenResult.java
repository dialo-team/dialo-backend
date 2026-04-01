package com.fit.se.application.token.refresh;

import lombok.Builder;

@Builder
public record RefreshTokenResult(
        String accessToken,
        String refreshToken,
        String tokenType
) {}
