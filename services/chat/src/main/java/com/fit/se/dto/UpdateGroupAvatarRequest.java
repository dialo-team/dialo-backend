package com.fit.se.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateGroupAvatarRequest {
    @NotBlank(message = "groupAvatarUrl must not be blank")
    private String groupAvatarUrl;
}
