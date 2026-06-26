package com.fit.se.common.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.Instant;

/**
 * @param timestamp`-
 * @param status    - Http status code (e.g., 200, 404, ...)
 * @param message   - Description of the outcome
 * @param data      - The actual data (can be null for error case)
 * @param metadata  - Optional metadata like pagination info
 */
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
        if(timestamp == null) timestamp = Instant.now();
    }
}
