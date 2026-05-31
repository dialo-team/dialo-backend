package com.fit.se.message.dto;

import lombok.Data;

@Data
public class ForwardMessageRequest {
    private String conversationId;
    private String targetUserId;
}
