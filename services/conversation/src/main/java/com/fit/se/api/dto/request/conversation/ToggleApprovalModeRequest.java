package com.fit.se.api.dto.request.conversation;

import jakarta.validation.constraints.NotNull;

public record ToggleApprovalModeRequest(
        @NotNull Boolean approvalRequired
) {
}
