package com.fit.se.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
        @NotBlank(message = "Refresh token khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng")
        String refreshToken
) {}

