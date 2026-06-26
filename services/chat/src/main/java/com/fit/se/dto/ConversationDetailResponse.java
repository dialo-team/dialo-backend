package com.fit.se.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class ConversationDetailResponse {
    private String conversationId;
    private String requesterId;
    private String counterpartId;
    private String counterpartName;
    private String counterpartAvatarUrl;
    private String remarkName;
    private String groupDescription;
    private List<PinnedMessageResponse> pinnedMessages;
    private Integer unreadCount;
    private String unreadDisplay;
    private Instant createdAt;
    private List<MessageResponse> messages;
}
