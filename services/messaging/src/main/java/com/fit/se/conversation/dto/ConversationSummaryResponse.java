package com.fit.se.conversation.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class ConversationSummaryResponse {
    private String conversationId;
    private String type;
    private String counterpartId;
    private String counterpartName;
    private String counterpartAvatarUrl;
    private String groupName;
    private String groupAvatarUrl;
    private String ownerId;
    private String description;
    private Integer activeMemberCount;
    private String myRole;
    private Boolean blocked;
    private String lastMessageId;
    private String lastMessage;
    private String lastMessagePreview;
    private String lastMessageSenderId;
    private String lastMessageType;
    private Instant lastMessageAt;
    private Boolean lastMessageSystem;
    private Integer unreadCount;
    private String unreadDisplay;
}
