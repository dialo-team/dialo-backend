package com.fit.se.api.dto.response.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.Instant;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        Instant timestamp,
        int status,
        String message,
        T data,
        Object metadata
) {
    public ApiResponse {
        if (timestamp == null) timestamp = Instant.now();
    }
}
