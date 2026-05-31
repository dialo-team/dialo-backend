package com.fit.se.common.exception.errors;

import com.fit.se.common.exception.DomainException;

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

