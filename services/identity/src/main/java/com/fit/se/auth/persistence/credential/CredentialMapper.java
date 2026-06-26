package com.fit.se.auth.persistence.credential;

import com.fit.se.auth.domain.credential.Credential;
import com.fit.se.auth.persistence.account.AccountEntity;
import org.springframework.stereotype.Component;

@Component
public class CredentialMapper {
    public CredentialEntity toEntity(Credential domain) {
        return CredentialEntity.builder()
                .id(domain.getId())
                .secretData(domain.getSecretData())
                .salt(domain.getSalt())
                .credentialData(domain.getCredentialData())
                .priority(domain.getPriority())
                .version(domain.getVersion())
                .type(domain.getType())
                .user(AccountEntity.builder()
                        .id(domain.getUserId())
                        .build())
                .build();
    }

    public Credential toDomain(CredentialEntity entity) {
        return Credential.create(entity.getId(), entity.getSecretData(), entity.getType(), entity.getCredentialData(), entity.getSalt(), entity.getUser().getId(), entity.getVersion());
    }
}

