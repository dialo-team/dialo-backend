package com.fit.se.exception.errors;

import com.fit.se.exception.DomainException;
import org.springframework.validation.FieldError;

import java.util.List;

public class AccountNotFoundException extends DomainException {
    public AccountNotFoundException() {
        super(
                401,
                "ACCOUNT_NOT_FOUND",
                "account not found",
                null
        );
    }
}
