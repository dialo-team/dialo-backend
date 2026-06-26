package com.fit.se.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RevokeRequest(
        @NotBlank(message = "Refresh token khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng")
        String refreshToken,
        @NotBlank(message = "MÃ£ phiÃªn Ä‘Äƒng nháº­p khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng")
        String sessId
) {}

