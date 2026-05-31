package com.fit.se.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class ConversationSummaryResponse {
    private String conversationId;
    private String counterpartId;
    private String counterpartName;
    private String counterpartAvatarUrl;
    private String lastMessageId;
    private String lastMessage;
    private String lastMessageSenderId;
    private String lastMessageType;
    private Instant lastMessageAt;
    private Boolean lastMessageSystem;
    private Integer unreadCount;
    private String unreadDisplay;
}
