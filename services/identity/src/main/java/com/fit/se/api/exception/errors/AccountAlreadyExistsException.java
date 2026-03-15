package com.fit.se.api.exception.errors;

import com.fit.se.api.exception.DomainException;

public class AccountAlreadyExistsException extends DomainException {
    public AccountAlreadyExistsException() {
        super(
                409,
                "ACCOUNT_ALREADY_EXISTS",
                "",
                null
        );
    }
}
