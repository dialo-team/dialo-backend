package com.fit.se.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LastMessageSummary {
    private String messageId;
    private String senderId;
    private String content;
    private String type;
    private Boolean system;
    private Instant createdAt;
}
