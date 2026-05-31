package com.fit.se.user.application.command.changepass;

import com.fit.se.auth.domain.account.AccountRepository;
import com.fit.se.auth.domain.credential.Credential;
import com.fit.se.auth.domain.credential.CredentialRepository;
import com.fit.se.common.security.hasher.BcryptHasher;
import com.fit.se.common.security.hasher.HasherFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UPasswordCommandHandler {
    private final AccountRepository userRepo;
    private final CredentialRepository credentialRepo;
    private final HasherFactory hasher;

    public void execute(UPasswordCommand cmd) {
        String subject = cmd.auth().getName();

        System.out.println("subject: " + subject);

        Credential credential = credentialRepo.findByUser(subject);
        if (credential == null) {
            throw new RuntimeException("KhÃ´ng tÃ¬m tháº¥y credential");
        }

        boolean matched = hasher.matches(
                cmd.oldPass(),
                credential.getSecretData(),
                null
        );

        if (!matched) {
            throw new RuntimeException("Máº­t kháº©u cÅ© khÃ´ng khá»›p");
        }

        String salt = hasher.generateSalt();
        String hashPassword = hasher.hash(cmd.newPass(), salt);

        credential.rotateSecret(hashPassword, salt.getBytes());
        credentialRepo.save(credential);
    }
}

