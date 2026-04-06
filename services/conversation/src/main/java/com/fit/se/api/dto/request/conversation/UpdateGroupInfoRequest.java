package com.fit.se.api.dto.request.conversation;

import jakarta.validation.constraints.Size;

public record UpdateGroupInfoRequest(
        @Size(max = 255) String name,
        @Size(max = 1024) String avatarUrl
) {
}
