package com.fit.se.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class TypingIndicatorResponse {
    private String conversationId;
    private String userId;
    private String displayName;
    private boolean typing;
    private Instant occurredAt;
}
