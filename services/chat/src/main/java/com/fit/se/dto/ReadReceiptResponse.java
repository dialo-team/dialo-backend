package com.fit.se.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class ReadReceiptResponse {
    private String userId;
    private String displayName;
    private String avatarUrl;
    private Instant readAt;
}
