package com.fit.se.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class PollResponse {
    private String title;
    private boolean closed;
    private Instant closedAt;
    private String closedByUserId;
    private List<PollOptionResponse> options;
}
