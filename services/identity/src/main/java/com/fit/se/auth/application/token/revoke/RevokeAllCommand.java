package com.fit.se.auth.application.token.revoke;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record RevokeAllCommand(
        @NotBlank(message = "Refresh token khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng")
        String refreshToken
) {}

