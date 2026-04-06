package com.fit.se.api.dto.request.join;

import jakarta.validation.constraints.NotBlank;

public record JoinGroupByTokenRequest(
        @NotBlank String token
) {
}
