package com.fit.se.application.token.revoke;

import lombok.Builder;

@Builder
public record RevokeCommand(
        String subject,
        String refreshToken,
        String ipAddress
) {}
