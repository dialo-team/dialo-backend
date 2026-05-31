package com.fit.se.conversation.dto;

import com.fit.se.message.dto.MessageResponse;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class ConversationDetailResponse {
    private String conversationId;
    private String type;
    private String counterpartId;
    private String counterpartName;
    private String counterpartAvatarUrl;
    private String groupName;
    private String groupAvatarUrl;
    private String ownerId;
    private String description;
    private Instant createdAt;
    private Integer activeMemberCount;
    private String myRole;
    private Boolean blocked;
    private String blockedMessage;
    private List<ConversationMemberResponse> members;
    private Integer unreadCount;
    private List<MessageResponse> messages;
}
