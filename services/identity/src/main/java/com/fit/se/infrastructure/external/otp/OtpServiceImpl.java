package com.fit.se.infrastructure.external.otp;

import com.fit.se.api.exception.errors.OtpExpiredException;
import com.fit.se.domain.otp.OtpType;
import com.fit.se.infrastructure.cache.CacheStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {
    private final CacheStore cache;
    private final SecureNumericCodeGenerator generator;
    private final OtpSender otpSender;

    @Override
    public int send(String destination, OtpType type) {
        int expiredTime = 1000 * 60 * 5;
        String otp = generator.generate(6);
        cache.put(destination, otp, expiredTime);
        otpSender.send(destination, type, otp);
        return expiredTime;
    }

    @Override
    public void reSend(String phone) {
        String otp = generator.generate(6);
        cache.delete(phone);
        cache.put(phone, otp, 1000 * 60 * 5);
    }

    @Override
    public boolean validate(String phone, String otp) {
        String currOtp = cache.get(phone)
                .orElseThrow(OtpExpiredException::new);
        return currOtp.equals(otp);
    }
}
