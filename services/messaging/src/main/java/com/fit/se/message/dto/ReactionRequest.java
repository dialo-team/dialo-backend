package com.fit.se.message.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReactionRequest {
    @NotNull
    private Integer emoji;
}
