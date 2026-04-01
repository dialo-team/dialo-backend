package com.fit.se.application.token.revoke;

import lombok.Builder;

@Builder
public record RevokeAllCommand(
   String refreshToken
) {}
