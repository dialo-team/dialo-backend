package com.fit.se.api.dto.request.label;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RenameUserLabelRequest(
        @NotBlank @Size(max = 100) String name
) {
}
