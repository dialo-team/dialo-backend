package com.fit.se.common.exception;

import com.fit.se.common.dto.response.ApiError;
import org.springframework.stereotype.Component;

@Component
public class ExceptionMapper {
    public ApiError toApiError(DomainException ex) {
        return ApiError.builder()
                .status(ex.getStatus())
                .path(ex.getPath())
                .message(ex.getMessage())
                .errors(ex.getErrors())
                .build();
    }
}
