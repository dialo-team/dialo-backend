package com.fit.se.conversation.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransferGroupOwnerResponse {
    private String conversationId;
    private String previousOwnerUserId;
    private String newOwnerUserId;
    private Boolean transferred;
}
