package com.fit.se.application.user.query.otp;

import com.fit.se.infrastructure.otp.SecureNumericCodeGenerator;
import com.fit.se.infrastructure.redis.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SendOTPQueryHandler {
    private final RedisService redisService;
    private final SecureNumericCodeGenerator generator;

    public void execute(String phone) {
        String otp = generator.generate(6);
        redisService.delete(phone);
        redisService.setEx(phone, otp, 1000 * 60 * 5);
    }
}
