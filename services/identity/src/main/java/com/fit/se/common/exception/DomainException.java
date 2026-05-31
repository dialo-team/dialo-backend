package com.fit.se.common.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.validation.FieldError;

import java.util.List;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class DomainException extends RuntimeException {
    final int status;
    final String path;
    final String message;
    final List<FieldError> errors;
}

