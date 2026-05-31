package com.fit.se.auth.domain.credential;

public interface CredentialRepository {
    public void save(Credential credential);

    Credential findByUser(String id);

    boolean existBySecretData(String password, String type);
}

