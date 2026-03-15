package com.fit.se.api.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Builder;
import org.springframework.validation.FieldError;

import java.time.Instant;
import java.util.List;

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
   @GeneratedValue(strategy = GenerationType.UUID) String traceId
) {
    public ApiError {
        if(timestamp == null) timestamp = Instant.now();
    }
}
