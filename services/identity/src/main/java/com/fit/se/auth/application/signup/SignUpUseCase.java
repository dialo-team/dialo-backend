package com.fit.se.auth.application.signup;

import com.fit.se.event.model.UserCreatedEvent;
import com.fit.se.event.producer.UserEventPublisher;
import com.fit.se.common.exception.errors.AccountAlreadyExistsException;
import com.fit.se.common.exception.errors.OtpExpiredException;
import com.fit.se.auth.domain.otp.OtpType;
import com.fit.se.auth.domain.account.Account;
import com.fit.se.auth.domain.account.AccountRepository;
import com.fit.se.auth.domain.credential.Credential;
import com.fit.se.auth.domain.credential.CredentialRepository;
import com.fit.se.auth.infrastructure.otp.OtpService;
import com.fit.se.common.security.hasher.BcryptHasher;
import com.fit.se.common.security.hasher.HasherFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignUpUseCase {
    private final OtpService otpService;
    private final AccountRepository accountRepo;
    private final CredentialRepository credentialRepo;
    private final UserEventPublisher publisher;

    public void request(String phone) {
        boolean isExist = accountRepo.existsByPhone(phone);
        if (isExist) {
            throw new AccountAlreadyExistsException();
        }

        otpService.send(phone, OtpType.SMS);
    }

    @Transactional
    public void execute(String phone, String password, String otp) {
        boolean isFail = !otpService.validate(phone, otp);
        if (isFail) {
            throw new OtpExpiredException();
        }

        Account draftAccount = Account.create(phone);
        draftAccount.enabled();
        String accountId = accountRepo.save(draftAccount);

        HasherFactory hasher = new BcryptHasher();
        String salt = hasher.generateSalt();
        String hashPassword = hasher.hash(password, salt);
        Credential newCredential = Credential.create(
                hashPassword,
                "PASSWORD",
                "",
                salt.getBytes(),
                accountId
        );
        credentialRepo.save(newCredential);

        publisher.publishUserCreated(UserCreatedEvent.builder()
                .userId(accountId)
                .phone(phone)
                .build());
    }
}
