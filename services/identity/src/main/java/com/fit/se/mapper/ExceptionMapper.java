package com.fit.se.mapper;

import com.fit.se.dto.response.ApiError;
import com.fit.se.exception.DomainException;
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
