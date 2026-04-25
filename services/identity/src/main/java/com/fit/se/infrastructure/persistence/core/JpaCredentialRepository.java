package com.fit.se.infrastructure.persistence.core;

import com.fit.se.domain.user.Credential;
import com.fit.se.domain.user.CredentialRepository;
import com.fit.se.infrastructure.security.hasher.BcryptHasher;
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
