package com.fit.se.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ClearHistoryRequest {
    @NotBlank
    private String userId;
}
