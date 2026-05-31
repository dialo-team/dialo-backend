package com.fit.se.auth.persistence.credential;

import com.fit.se.auth.domain.credential.Credential;
import com.fit.se.auth.domain.credential.CredentialRepository;
import com.fit.se.common.security.hasher.BcryptHasher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaCredentialRepository implements CredentialRepository {
    private final SpringDataCredentialRepository credentialRepo;
    private final CredentialMapper credentialMapper;
    private final BcryptHasher hasher;

    @Override
    public void save(Credential credential) {
        credentialRepo.save(credentialMapper.toEntity(credential));
    }

    @Override
    public Credential findByUser(String userId) {
        return credentialMapper.toDomain(credentialRepo.findByUser_Id(userId));
    }

    @Override
    public boolean existBySecretData(String password, String type) {

        credentialRepo.existsBySecretDataAndType(password, type);
        return false;
    }
}

