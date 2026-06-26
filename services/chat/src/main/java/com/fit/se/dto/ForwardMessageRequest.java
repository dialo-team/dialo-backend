package com.fit.se.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ForwardMessageRequest {
    private String senderId;
    @NotBlank
    private String targetConversationId;
    @NotBlank
    private String sourceMessageId;
}
