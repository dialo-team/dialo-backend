package com.fit.se.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateGroupDescriptionRequest {
    @NotBlank(message = "groupDescription must not be blank")
    private String groupDescription;
}
