package com.fit.se.application.user.command.signup;

import com.fit.se.domain.otp.OtpType;
import com.fit.se.domain.user.Account;
import com.fit.se.domain.user.AccountRepository;
import com.fit.se.api.exception.errors.AccountAlreadyExistsException;
import com.fit.se.infrastructure.otp.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignUpCommandHandler {
    private final AccountRepository accountRepo;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;

    public void execute(SignUpCommand request) {
        if (accountRepo.existsByPhone(request.phone()))
            throw new AccountAlreadyExistsException();
        Account account = Account.builder()
                .phone(request.phone())
                .password(passwordEncoder.encode(request.password()))
                .enabled(false)
                .locked(false)
                .build();
        accountRepo.save(account);

        otpService.send(request.phone(), OtpType.SMS);
    }
}
