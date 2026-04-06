package com.fit.se.api.dto.request.bootstrap;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record BootstrapUserRequest(
        @NotNull @Positive Long userId
) {
}
