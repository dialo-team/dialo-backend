package com.fit.se.infrastructure.persistence.core;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataCredentialRepository extends JpaRepository<CredentialEntity, String> {
    CredentialEntity findByUser_Id(String userId);
}
