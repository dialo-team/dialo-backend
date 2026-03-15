package com.fit.se.api.dto.request;

import com.fit.se.domain.otp.OtpType;

public record SendOtpRequest(
        String phone,
        OtpType type
) {}
