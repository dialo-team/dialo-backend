package com.fit.se.auth.application.changepass;

import com.fit.se.auth.domain.credential.Credential;
import com.fit.se.auth.domain.credential.CredentialRepository;
import com.fit.se.common.security.hasher.HasherFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChangePasswordUseCase {
    private final CredentialRepository credentialRepo;
    private final HasherFactory hasher;

    public void execute(String accountId, String newPass, String oldPass) {

        Credential credential = credentialRepo.findByUser(accountId);
        if (credential == null) {
            throw new RuntimeException("KhÃ´ng tÃ¬m tháº¥y credential");
        }

        boolean matched = hasher.matches(
                oldPass,
                credential.getSecretData(),
                null
        );

        if (!matched) {
            throw new RuntimeException("Máº­t kháº©u cÅ© khÃ´ng khá»›p");
        }

        String salt = hasher.generateSalt();
        String hashPassword = hasher.hash(newPass, salt);

        credential.rotateSecret(hashPassword, salt.getBytes());
        credentialRepo.save(credential);
    }
}

