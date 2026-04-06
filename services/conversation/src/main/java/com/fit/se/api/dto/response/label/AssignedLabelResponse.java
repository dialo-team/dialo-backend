package com.fit.se.api.dto.response.label;

public record AssignedLabelResponse(
        Long labelId,
        String name,
        String color
) {
}
