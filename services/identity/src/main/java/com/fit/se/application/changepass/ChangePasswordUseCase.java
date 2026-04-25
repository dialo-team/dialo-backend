package com.fit.se.application.changepass;

import com.fit.se.domain.user.Credential;
import com.fit.se.domain.user.CredentialRepository;
import com.fit.se.infrastructure.security.hasher.HasherFactory;
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
            throw new RuntimeException("Không tìm thấy credential");
        }

        boolean matched = hasher.matches(
                oldPass,
                credential.getSecretData(),
                null
        );

        if (!matched) {
            throw new RuntimeException("Mật khẩu cũ không khớp");
        }

        String salt = hasher.generateSalt();
        String hashPassword = hasher.hash(newPass, salt);

        credential.rotateSecret(hashPassword, salt.getBytes());
        credentialRepo.save(credential);
    }
}
