package com.fit.se.conversation.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateGroupMemberRoleResponse {
    private String conversationId;
    private String userId;
    private String role;
}
