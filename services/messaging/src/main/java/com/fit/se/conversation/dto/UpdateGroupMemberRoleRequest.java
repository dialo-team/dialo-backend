package com.fit.se.conversation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateGroupMemberRoleRequest {
    @NotBlank
    private String role;
}
