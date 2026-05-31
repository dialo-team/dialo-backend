package com.fit.se.conversation.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class AddGroupMembersRequest {
    @NotEmpty
    private List<String> participantIds;
}
