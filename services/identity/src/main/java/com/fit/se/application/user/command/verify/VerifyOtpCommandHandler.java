package com.fit.se.application.user.command.verify;

import com.fit.se.application.user.event.UserCreatedEvent;
import com.fit.se.application.user.event.UserEventPublisher;
import com.fit.se.domain.user.Account;
import com.fit.se.domain.user.AccountRepository;
import com.fit.se.domain.user.Credential;
import com.fit.se.domain.user.CredentialRepository;
import com.fit.se.infrastructure.cache.CacheStore;
import com.fit.se.infrastructure.external.otp.OtpService;
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
