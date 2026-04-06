package com.fit.se.api.dto.request.join;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RejectJoinRequestRequest(
        @NotBlank String joinRequestId,
        @Size(max = 500) String reason
) {
}
