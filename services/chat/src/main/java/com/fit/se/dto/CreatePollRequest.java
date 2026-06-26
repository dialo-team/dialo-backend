package com.fit.se.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class CreatePollRequest {
    @NotBlank
    private String conversationId;
    @NotBlank
    private String title;
    @Size(min = 2)
    private List<@NotBlank String> options;
}
