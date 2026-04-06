package com.fit.se.api.dto.request.settings;

import jakarta.validation.constraints.Size;

public record ChangeConversationAliasRequest(
        @Size(max = 255) String alias
) {
}
