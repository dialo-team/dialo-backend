package com.fit.se.infrastructure.otp;

import com.fit.se.api.dto.request.OtpValidateRequest;
import com.fit.se.domain.otp.OtpType;

public interface OtpService {
    public void send(String destination, OtpType type);
    public void reSend(String phone);
    public boolean validate(OtpValidateRequest request);
}
