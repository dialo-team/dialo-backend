package com.fit.se.application.credential.forgot;

import com.fit.se.domain.otp.OtpType;
import com.fit.se.domain.user.Account;
import com.fit.se.domain.user.AccountRepository;
import com.fit.se.infrastructure.cache.CacheStore;
import com.fit.se.infrastructure.external.otp.OtpService;
import com.fit.se.infrastructure.security.token.TokenProvider;
import com.fit.se.infrastructure.security.token.TokenPurpose;
import com.fit.se.infrastructure.security.token.TokenStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ForgotCommandHandler {
    private final AccountRepository userRepo;
    private final OtpService otpService;

    public void execute(ForgotByPhoneCommand cmd) {
        if(!userRepo.existsByPhone(cmd.phone())) {
            throw new RuntimeException("");
        }

        int expiredTime = otpService.send(cmd.phone(), OtpType.SMS);
    }
}
