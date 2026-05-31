package com.fit.se.conversation.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DissolveGroupResponse {
    private String conversationId;
    private Boolean dissolved;
}
