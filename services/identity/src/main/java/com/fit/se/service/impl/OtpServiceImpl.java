package com.fit.se.service.impl;

import com.fit.se.dto.request.OtpValidateRequest;
import com.fit.se.exception.errors.OtpExpiredException;
import com.fit.se.model.OtpType;
import com.fit.se.service.OtpSender;
import com.fit.se.service.OtpService;
import com.fit.se.service.RedisService;
import com.fit.se.util.SecureNumericCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {
    private final RedisService redisService;
    private final SecureNumericCodeGenerator generator;
    private final OtpSender otpSender;

    @Override
    public void send(String destination, OtpType type) {
        String otp = generator.generate(6);
        redisService.setEx(destination, otp, 1000 * 60 * 5);
        otpSender.send(destination, type, otp);
    }

    @Override
    public void reSend(String phone) {
        String otp = generator.generate(6);
        redisService.delete(phone);
        redisService.setEx(phone, otp, 1000 * 60 * 5);
    }

    @Override
    public boolean validate(OtpValidateRequest request) {
        String currOtp = redisService.get(request.phone())
                .orElseThrow(OtpExpiredException::new);
        return currOtp.equals(request.otp());
    }
}
