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

    public void execute(UPasswordCommand cmd) {
        Credential credential = credentialRepo.findByUser(cmd.userId());
        HasherFactory hasher = new BcryptHasher();
        String salt = hasher.generateSalt();
        String hashPassword = hasher.hash(cmd.newPass(), salt);
        credential.rotateSecret(hashPassword, salt.getBytes());
        credentialRepo.save(credential);
    }
}
