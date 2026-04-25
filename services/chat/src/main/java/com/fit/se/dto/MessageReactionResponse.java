package com.fit.se.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class MessageReactionResponse {
    private String userId;
    private String displayName;
    private String avatarUrl;
    private String emoji;
    private Instant reactedAt;
}
