package com.fit.se.api.dto.request.join;

import jakarta.validation.constraints.NotBlank;

public record CancelJoinRequestRequest(
        @NotBlank String joinRequestId
) {
}
