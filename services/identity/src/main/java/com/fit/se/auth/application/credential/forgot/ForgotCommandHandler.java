package com.fit.se.auth.application.credential.forgot;

import com.fit.se.auth.domain.otp.OtpType;
import com.fit.se.auth.domain.account.Account;
import com.fit.se.auth.domain.account.AccountRepository;
import com.fit.se.common.cache.CacheStore;
import com.fit.se.auth.infrastructure.otp.OtpService;
import com.fit.se.common.security.token.TokenProvider;
import com.fit.se.common.security.token.TokenPurpose;
import com.fit.se.common.security.token.TokenStore;
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

