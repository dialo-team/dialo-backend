package com.fit.se.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateRemarkRequest {
    @NotBlank
    private String requesterId;
    @NotBlank
    private String remarkName;
}
