package com.fit.se.auth.application.token.revoke;

import lombok.Builder;

@Builder
public record RevokeCommand(
        String subject,
        String refreshToken,
        String ipAddress
) {}

