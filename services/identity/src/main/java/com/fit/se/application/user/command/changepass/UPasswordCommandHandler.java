package com.fit.se.application.user.command.changepass;

import com.fit.se.domain.user.AccountRepository;
import com.fit.se.domain.user.Credential;
import com.fit.se.domain.user.CredentialRepository;
import com.fit.se.infrastructure.security.hasher.BcryptHasher;
import com.fit.se.infrastructure.security.hasher.HasherFactory;
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
            throw new RuntimeException("Không tìm thấy credential");
        }

        boolean matched = hasher.matches(
                cmd.oldPass(),
                credential.getSecretData(),
                null
        );

        if (!matched) {
            throw new RuntimeException("Mật khẩu cũ không khớp");
        }

        String salt = hasher.generateSalt();
        String hashPassword = hasher.hash(cmd.newPass(), salt);

        credential.rotateSecret(hashPassword, salt.getBytes());
        credentialRepo.save(credential);
    }
}
