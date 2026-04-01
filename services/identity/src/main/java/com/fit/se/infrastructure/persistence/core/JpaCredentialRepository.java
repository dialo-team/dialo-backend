package com.fit.se.infrastructure.persistence.core;

import com.fit.se.domain.user.Credential;
import com.fit.se.domain.user.CredentialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaCredentialRepository implements CredentialRepository {
    private final SpringDataCredentialRepository credentialRepo;
    private final CredentialMapper credentialMapper;

    @Override
    public void save(Credential credential) {
        credentialRepo.save(credentialMapper.toEntity(credential));
    }

    @Override
    public Credential findByUser(String userId) {
        return credentialMapper.toDomain(credentialRepo.findByUser_Id(userId));
    }
}
