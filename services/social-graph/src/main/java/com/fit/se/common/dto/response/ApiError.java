package com.fit.se.common.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import org.springframework.validation.FieldError;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * @param timestamp -
 * @param status    - Http status code (e.g., 200, 404, ...)
 * @param path      - where error occurs
 * @param message   - Description of the outcome
 * @param errors    - Optional metadata like pagination info
 */
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(
   Instant timestamp,
   int status,
   String path,
   String message,
   List<FieldError> errors,
   String traceId
) {
    public ApiError {
        if (timestamp == null) timestamp = Instant.now();
        if (traceId == null || traceId.isBlank()) {
            traceId = UUID.randomUUID().toString();
        }
    }
}
