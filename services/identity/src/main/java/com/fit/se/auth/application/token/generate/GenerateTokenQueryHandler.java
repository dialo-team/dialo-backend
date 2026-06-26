package com.fit.se.auth.application.token.generate;

import com.fit.se.auth.domain.account.Account;
import com.fit.se.auth.domain.account.AccountRepository;
import com.fit.se.auth.domain.otp.OtpType;
import com.fit.se.common.cache.CacheStore;
import com.fit.se.auth.infrastructure.otp.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class GenerateTokenQueryHandler {
    private final AuthenticationManager authManager;
    private final AccountRepository userRepo;
    private final CacheStore cache;
    private final OtpService otpService;

    public void execute(GenerateTokenQuery query) {
        Account user = userRepo.findByPhone(query.phone()).orElseThrow();

        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getId(), query.password())
        );

        otpService.send(query.phone(), OtpType.SMS);

        String key = "signin-draft:" + query.phone();
        cache.putHash(key, Map.of(
                "subject", user.getId(),
                "phone", user.getPhone(),
                "ipAddress", query.ipAddress(),
                "deviceName", query.deviceName(),
                "loginMethod", query.loginMethod()
        ));
    }
}

