package com.fit.se.auth.application.token.generate;

import lombok.Builder;

@Builder
public record GenerateTokenQuery(
        String phone,
        String password,
        String deviceName,
        String ipAddress,
        String loginMethod
) {}

