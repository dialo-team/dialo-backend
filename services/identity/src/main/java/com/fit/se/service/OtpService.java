package com.fit.se.service;

import com.fit.se.dto.request.OtpValidateRequest;
import com.fit.se.model.OtpType;

public interface OtpService {
    public void send(String destination, OtpType type);
    public void reSend(String phone);
    public boolean validate(OtpValidateRequest request);
}
