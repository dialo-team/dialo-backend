package com.fit.se.api.dto.request.settings;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AssignConversationLabelRequest(
        @NotNull @Positive Long labelId
) {
}
