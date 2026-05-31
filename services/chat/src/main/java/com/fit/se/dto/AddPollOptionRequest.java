package com.fit.se.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddPollOptionRequest {
    @NotBlank
    private String content;
}
