package com.fit.se.api.dto.response.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(
        Instant timestamp,
        int status,
        String path,
        String message,
        List<ValidationErrorDetail> errors,
        String traceId
) {
    public ApiError {
        if (timestamp == null) timestamp = Instant.now();
    }
}
