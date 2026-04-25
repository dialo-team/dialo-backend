package com.fit.se.application.signup;

import com.fit.se.api.exception.errors.AccountAlreadyExistsException;
import com.fit.se.api.exception.errors.OtpExpiredException;
import com.fit.se.application.user.event.UserCreatedEvent;
import com.fit.se.application.user.event.UserEventPublisher;
import com.fit.se.domain.otp.OtpType;
import com.fit.se.domain.user.Account;
import com.fit.se.domain.user.AccountRepository;
import com.fit.se.domain.user.Credential;
import com.fit.se.domain.user.CredentialRepository;
import com.fit.se.infrastructure.external.otp.OtpService;
import com.fit.se.infrastructure.security.hasher.BcryptHasher;
import com.fit.se.infrastructure.security.hasher.HasherFactory;
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
        if(isExist) {
            throw new AccountAlreadyExistsException();
        }

        otpService.send(phone, OtpType.SMS);
    }

    public void execute(String phone, String password, String otp) {
        boolean isFail = !otpService.validate(phone, otp);
        if(isFail) {
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
