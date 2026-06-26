package com.fit.se.conversation.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class ConversationMemberResponse {
    private String userId;
    private String displayName;
    private String avatarUrl;
    private String role;
    private Instant joinedAt;
    private Instant leftAt;
    private Boolean active;
}
