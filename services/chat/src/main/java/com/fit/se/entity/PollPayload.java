package com.fit.se.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PollPayload {
    private String title;
    @Builder.Default
    private List<PollOption> options = new ArrayList<>();
    @Builder.Default
    private boolean closed = false;
    private Instant closedAt;
    private String closedByUserId;
}
