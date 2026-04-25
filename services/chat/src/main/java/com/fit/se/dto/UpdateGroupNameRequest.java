package com.fit.se.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateGroupNameRequest {
    @NotBlank(message = "groupName must not be blank")
    private String groupName;
}