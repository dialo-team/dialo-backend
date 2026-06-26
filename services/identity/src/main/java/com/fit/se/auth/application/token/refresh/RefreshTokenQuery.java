package com.fit.se.auth.application.token.refresh;

import lombok.Builder;

@Builder
public record RefreshTokenQuery(
        String refreshToken,
        String ipAddress
) {}

