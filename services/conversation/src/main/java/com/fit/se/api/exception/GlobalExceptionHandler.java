package com.fit.se.api.exception;

import com.fit.se.api.context.RequestContextHolder;
import com.fit.se.api.dto.response.common.ApiError;
import com.fit.se.api.dto.response.common.ValidationErrorDetail;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final ApiExceptionMapper apiExceptionMapper;
    private final ErrorMessageResolver errorMessageResolver;

    public GlobalExceptionHandler(ApiExceptionMapper apiExceptionMapper, ErrorMessageResolver errorMessageResolver) {
        this.apiExceptionMapper = apiExceptionMapper;
        this.errorMessageResolver = errorMessageResolver;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> validation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<ValidationErrorDetail> errors = ex.getBindingResult().getFieldErrors().stream().map(this::toValidationDetail).toList();
        ApiError apiError = ApiError.builder().status(HttpStatus.BAD_REQUEST.value()).path(request.getRequestURI()).message("Validation failed").errors(errors).traceId(RequestContextHolder.getTraceId()).build();
        return ResponseEntity.badRequest().body(apiError);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> constraint(ConstraintViolationException ex, HttpServletRequest request) {
        List<ValidationErrorDetail> errors = ex.getConstraintViolations().stream()
                .map(v -> new ValidationErrorDetail(v.getPropertyPath().toString(), v.getInvalidValue(), v.getMessage())).toList();
        ApiError apiError = ApiError.builder().status(HttpStatus.BAD_REQUEST.value()).path(request.getRequestURI()).message("Validation failed").errors(errors).traceId(RequestContextHolder.getTraceId()).build();
        return ResponseEntity.badRequest().body(apiError);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> generic(Exception ex, HttpServletRequest request) {
        HttpStatus status = apiExceptionMapper.toStatus(ex);
        ApiError apiError = ApiError.builder().status(status.value()).path(request.getRequestURI()).message(errorMessageResolver.resolve(ex)).traceId(RequestContextHolder.getTraceId()).build();
        return ResponseEntity.status(status).body(apiError);
    }

    private ValidationErrorDetail toValidationDetail(FieldError fieldError) {
        return new ValidationErrorDetail(fieldError.getField(), fieldError.getRejectedValue(), fieldError.getDefaultMessage());
    }
}
