package com.fit.se.auth.persistence.credential;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataCredentialRepository extends JpaRepository<CredentialEntity, String> {
    CredentialEntity findByUser_Id(String userId);

    boolean existsBySecretDataAndType(String secretData, String type);
}

