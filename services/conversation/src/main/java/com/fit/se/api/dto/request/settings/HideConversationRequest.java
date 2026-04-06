package com.fit.se.api.dto.request.settings;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record HideConversationRequest(
        @NotBlank @Size(max = 100) String pinCode
) {
}
