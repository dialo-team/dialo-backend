package com.fit.se.message.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class MessageReadByResponse {
    private String userId;
    private String displayName;
    private String avatarUrl;
    private Instant readAt;
}
