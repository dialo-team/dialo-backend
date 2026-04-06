package com.fit.se.api.dto.request.conversation;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateDirectConversationRequest(
        @NotNull @Positive Long otherUserId
) {
}
