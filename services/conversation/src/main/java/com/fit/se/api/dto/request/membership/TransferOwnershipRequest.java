package com.fit.se.api.dto.request.membership;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record TransferOwnershipRequest(
        @NotNull @Positive Long newOwnerUserId
) {
}
