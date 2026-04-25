package com.fit.se.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class PinnedMessageResponse {
    private String messageId;
    private String pinnedByUserId;
    private String pinnedByName;
    private Instant pinnedAt;
    private MessageResponse message;
}
