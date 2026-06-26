package com.fit.se.conversation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class CreateGroupConversationRequest {
    @NotBlank
    private String name;
    @NotEmpty
    private List<String> participantIds;
    private String avatarUrl;
    private String description;
}
