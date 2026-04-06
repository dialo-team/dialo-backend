package com.fit.se.api.dto.response.membership;

public record MemberResponse(
        String membershipId,
        Long userId,
        String role,
        String status
) {
}
