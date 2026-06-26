package com.fit.se.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "conversations")
public class Conversation {
    @Id
    private String id;
    private String type;
    private List<String> participants;
    @Indexed(unique = true, sparse = true)
    private String participantKey;
    @Builder.Default
    private Map<String, String> remarks = new HashMap<>();
    @Builder.Default
    private Map<String, Integer> unreadCounts = new HashMap<>();
    private LastMessageSummary lastMessage;
    @Builder.Default
    private Map<String, Instant> clearedAt = new HashMap<>();
    private String createdBy;
    private String createdSource;
    private Instant createdAt;
    private Instant updatedAt;

    private String groupName;
    private String groupDescription;
    private String groupAvatarUrl;
    private Boolean dissolved;
    @Builder.Default
    private Map<String, String> memberRoles = new HashMap<>(); // userId -> OWNER | ADMIN | MEMBER
    @Builder.Default
    private Map<String, String> memberNicknames = new HashMap<>();
    @Builder.Default
    private List<GroupMemberHistory> memberHistories = new ArrayList<>();
    @Builder.Default
    private List<PinnedMessageEntry> pinnedMessages = new ArrayList<>();
}
