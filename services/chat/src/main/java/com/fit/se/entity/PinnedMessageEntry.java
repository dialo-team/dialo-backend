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
public class PinnedMessageEntry {
    private String messageId;
    private String pinnedByUserId;
    private Instant pinnedAt;
}
