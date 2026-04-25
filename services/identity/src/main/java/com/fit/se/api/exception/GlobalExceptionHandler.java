package com.fit.se.api.exception;

import com.fit.se.api.dto.response.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final ExceptionMapper exMapper;

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ApiError> handleDomainException(DomainException ex, HttpServletRequest request) {
        return ResponseEntity
                .status(ex.getStatus())
                .body(exMapper.toApiError(ex));
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public ApiError handleBadRequest(IllegalArgumentException ex) {
        return ApiError.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler(IllegalStateException.class)
    public ApiError handleConflict(IllegalStateException ex) {
        return ApiError.builder()
                .status(HttpStatus.CONFLICT.value())
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ApiError handleOptimistic(OptimisticLockingFailureException ex) {
        return ApiError.builder()
                .status(HttpStatus.CONFLICT.value())
                .message("Data was modified concurrently. Please try again.")
                .build();
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ApiError handleForbidden(AccessDeniedException ex) {
        return ApiError.builder()
                .status(HttpStatus.FORBIDDEN.value())
                .message("Forbidden")
                .build();
    }

    @ExceptionHandler(Exception.class)
    public ApiError handleInternal(Exception ex) {
        return ApiError.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("Internal server error")
                .build();
    }
}
