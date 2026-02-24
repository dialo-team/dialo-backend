package com.fit.se.dto.request;

import com.fit.se.model.OtpType;

public record SendOtpRequest(
        String phone,
        OtpType type
) {}
