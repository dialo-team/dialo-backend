package com.fit.se.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateMemberNicknameRequest {
    @NotBlank(message = "nickname must not be blank")
    private String nickname;
}
