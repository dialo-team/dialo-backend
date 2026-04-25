package com.fit.se.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MarkReadRequest {
    @NotBlank
    private String userId;
}
