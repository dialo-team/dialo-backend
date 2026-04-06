package com.fit.se.api.dto.response.label;

public record UserLabelResponse(
        Long labelId,
        String name,
        String color,
        String type,
        Boolean deletable
) {
}
