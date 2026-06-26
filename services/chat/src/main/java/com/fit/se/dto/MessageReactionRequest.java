package com.fit.se.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MessageReactionRequest {
    @NotBlank
    private String emoji;
}
