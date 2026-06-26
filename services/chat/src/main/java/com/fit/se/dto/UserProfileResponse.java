package com.fit.se.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserProfileResponse {
    private String id;
    private String displayName;
    private String avatarUrl;
}
