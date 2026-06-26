package com.fit.se.user.application.command.resetpass;

import com.fit.se.auth.domain.account.AccountRepository;
import com.fit.se.auth.domain.credential.Credential;
import com.fit.se.auth.domain.credential.CredentialRepository;
import com.fit.se.common.security.hasher.HasherFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResetPassHandler {
    private final AccountRepository userRepo;
    private final CredentialRepository credentialRepo;
    private final HasherFactory hasher;

    public void execute(ResetPassCommand cmd) {
        String subject = cmd.auth().getName();

        Credential credential = credentialRepo.findByUser(subject);
        if (credential == null) {
            throw new RuntimeException("KhÃ´ng tÃ¬m tháº¥y credential");
        }

        String salt = hasher.generateSalt();
        String hashPassword = hasher.hash(cmd.newPass(), salt);

        credential.rotateSecret(hashPassword, salt.getBytes());
        credentialRepo.save(credential);
    }
}

