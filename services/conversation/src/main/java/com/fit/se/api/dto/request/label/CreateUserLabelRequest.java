package com.fit.se.api.dto.request.label;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateUserLabelRequest(
        @NotBlank @Size(max = 100) String name,
        @NotBlank @Pattern(regexp = "^#[0-9A-Fa-f]{6}$") String color
) {
}
