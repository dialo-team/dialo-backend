package com.fit.se.application.user.command.resetpass;

import com.fit.se.domain.user.AccountRepository;
import com.fit.se.domain.user.Credential;
import com.fit.se.domain.user.CredentialRepository;
import com.fit.se.infrastructure.security.hasher.HasherFactory;
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
            throw new RuntimeException("Không tìm thấy credential");
        }

        String salt = hasher.generateSalt();
        String hashPassword = hasher.hash(cmd.newPass(), salt);

        credential.rotateSecret(hashPassword, salt.getBytes());
        credentialRepo.save(credential);
    }
}
