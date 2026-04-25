package com.fit.se.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class GroupMemberHistoryResponse {
    private String id;
    private String action;
    private String actorUserId;
    private String actorDisplayName;
    private String targetUserId;
    private String targetDisplayName;
    private String oldRole;
    private String newRole;
    private String description;
    private Instant createdAt;
}
