package com.fit.se.common.exception.errors;

import com.fit.se.common.exception.DomainException;

public class AccountProvisioningException extends DomainException {
    public AccountProvisioningException() {
        super(
                409,
                "ACCOUNT_PROFILE_PROVISIONING",
                "Tai khoan dang duoc khoi tao. Vui long thu lai sau vai giay",
                null
        );
    }
}
