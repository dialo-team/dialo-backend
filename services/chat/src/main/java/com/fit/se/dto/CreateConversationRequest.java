package com.fit.se.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class CreateConversationRequest {
    @NotEmpty
    private List<String> participantIds;
    private String createdBy;
    private String initialSystemMessage;
}
