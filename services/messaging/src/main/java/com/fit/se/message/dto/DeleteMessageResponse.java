package com.fit.se.message.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeleteMessageResponse {
    private String messageId;
    private String conversationId;
    private Boolean deletedForMe;
    private Boolean deletedForEveryone;
}
