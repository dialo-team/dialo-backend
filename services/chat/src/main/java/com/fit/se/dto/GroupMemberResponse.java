package com.fit.se.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GroupMemberResponse {
    private String userId;
    private String displayName;
    private String avatarUrl;
    private String role;
    private String nickname;
}
