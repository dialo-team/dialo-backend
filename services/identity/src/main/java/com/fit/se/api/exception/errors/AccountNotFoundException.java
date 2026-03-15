package com.fit.se.api.exception.errors;

import com.fit.se.api.exception.DomainException;

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
