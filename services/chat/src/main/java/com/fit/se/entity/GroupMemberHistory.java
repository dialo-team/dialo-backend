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
public class GroupMemberHistory {
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
