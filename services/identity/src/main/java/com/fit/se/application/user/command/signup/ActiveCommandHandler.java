package com.fit.se.application.user.command.signup;

import com.fit.se.api.exception.errors.OtpExpiredException;
import com.fit.se.domain.user.Account;
import com.fit.se.domain.user.AccountRepository;
import com.fit.se.infrastructure.redis.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ActiveCommandHandler {
    private final AccountRepository accountRepo;
    private final RedisService redisService;

    public boolean execute(String phone, String otp) {
        if(!validate(phone, otp)) {
            return false;
        }
        Account account = accountRepo.findByPhone(phone).orElseThrow();
        account.setEnabled(true);
        return true;
    }

    private boolean validate(String phone, String otp) {
        String currOtp = redisService.get(phone)
                .orElseThrow(OtpExpiredException::new);
        return currOtp.equals(otp);
    }
}
