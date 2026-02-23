package com.fit.se.exception.errors;

import com.fit.se.exception.DomainException;

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
