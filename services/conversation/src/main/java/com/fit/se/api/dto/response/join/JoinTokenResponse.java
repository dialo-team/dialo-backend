package com.fit.se.api.dto.response.join;

public record JoinTokenResponse(
        String conversationId,
        String token,
        String qrContent
) {
}
