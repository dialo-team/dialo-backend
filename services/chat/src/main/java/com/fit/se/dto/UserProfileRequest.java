package com.fit.se.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserProfileRequest {
    @NotBlank
    private String id;
    @NotBlank
    private String displayName;
    private String avatarUrl;
}
