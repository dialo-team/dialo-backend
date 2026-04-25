package com.fit.se.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PollOptionResponse {
    private String id;
    private String content;
    private List<UserProfileResponse> voters;
}
