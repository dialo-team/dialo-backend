package com.fit.se.infrastructure.external.otp;

import com.fit.se.api.dto.request.OtpValidateRequest;
import com.fit.se.domain.otp.OtpType;

public interface OtpService {
    public int send(String destination, OtpType type);
    public void reSend(String phone);
    public boolean validate(String phone, String otp);
}
