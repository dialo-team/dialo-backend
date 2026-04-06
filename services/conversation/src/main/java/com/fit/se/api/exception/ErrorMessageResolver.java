package com.fit.se.api.exception;

import org.springframework.stereotype.Component;

@Component
public class ErrorMessageResolver {
    public String resolve(Throwable throwable) {
        return throwable.getMessage() != null ? throwable.getMessage() : "Unexpected error";
    }
}
