package com.fit.se.application.user.command.signup;

import com.fit.se.api.exception.errors.AccountAlreadyExistsException;
import com.fit.se.domain.user.AccountRepository;
import com.fit.se.domain.otp.OtpType;
import com.fit.se.infrastructure.cache.CacheStore;
import com.fit.se.infrastructure.external.otp.OtpService;
import com.fit.se.infrastructure.security.hasher.BcryptHasher;
import com.fit.se.infrastructure.security.hasher.HasherFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class SignUpCommandHandler {
    private final AccountRepository userRepo;
    private final OtpService otpService;
    private final CacheStore cache;

    /**
     * Flow:
     * 1. Check if the account exists.
     * 2. If it does not exist, create an inactive account and stop there.
     * 3. Send the OTP by SMS.
     */
    public void execute(SignUpCommand cmd) {
        if(userRepo.existsByPhone(cmd.phone())) {
            throw new AccountAlreadyExistsException();
        }

        HasherFactory hasher = new BcryptHasher();
        String salt = hasher.generateSalt();
        String hashPassword = hasher.hash(cmd.password(), salt);

        Map<String, String> draftUser = Map.of(
                "secretData", hashPassword,
                "salt", salt,
                "type", "PASSWORD"
        );
        String key = "draft-user:" + cmd.phone();

        int expiredTime = otpService.send(cmd.phone(), OtpType.SMS);

        cache.putHash(key, draftUser);
        cache.expire(key, expiredTime);
    }
}
