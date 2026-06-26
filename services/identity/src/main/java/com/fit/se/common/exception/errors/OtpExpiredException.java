package com.fit.se.common.exception.errors;

import com.fit.se.common.exception.DomainException;

public class OtpExpiredException extends DomainException {
    public OtpExpiredException() {
        super(
                401,
                "OTP_EXPIRED",
                "OTP has expired",
                null
        );
    }
}

