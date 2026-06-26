package com.fit.se.user.application.command.verify;

import com.fit.se.event.model.UserCreatedEvent;
import com.fit.se.event.producer.UserEventPublisher;
import com.fit.se.auth.domain.account.Account;
import com.fit.se.auth.domain.account.AccountRepository;
import com.fit.se.auth.domain.credential.Credential;
import com.fit.se.auth.domain.credential.CredentialRepository;
import com.fit.se.common.cache.CacheStore;
import com.fit.se.auth.infrastructure.otp.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class VerifyOtpCommandHandler {
    private final OtpService otpService;
    private final AccountRepository userRepo;
    private final CacheStore cache;
    private final CredentialRepository credentialRepo;
    private final UserEventPublisher publisher;

    /**
     *
     * @param cmd
     */
    public boolean execute(VerifyOTPCommand cmd) {
        boolean isSuccess = otpService.validate(cmd.phone(), cmd.otp());
        if (isSuccess) {
            Account user = Account.create(cmd.phone());
            user.enabled();
            String userId = userRepo.save(user);

            String key = "draft-user:" + cmd.phone();
            Map<Object, Object> draftUser = cache.getHash(key);
            String secretData = (String) draftUser.get("secretData");
            String salt = (String) draftUser.get("salt");
            String type = (String) draftUser.get("type");

            Credential password = Credential.create(
                    secretData,
                    type,
                    "",
                    salt.getBytes(),
                    userId
            );
            credentialRepo.save(password);

            publisher.publishUserCreated(UserCreatedEvent.builder()
                            .userId(userId)
                            .phone(cmd.phone())
                    .build());

            return true;
        }
        return false;
    }
}

