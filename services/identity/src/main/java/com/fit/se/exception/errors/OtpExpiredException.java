package com.fit.se.exception.errors;

import com.fit.se.exception.DomainException;

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
