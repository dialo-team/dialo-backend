package com.fit.se.auth.domain.credential;

import lombok.Getter;


@Getter
public class Credential {
    private String id;

    private String type;
    private String secretData;
    private byte[] salt;
    private String credentialData;
    private int priority;
    private int version;

    private String userId;

    public void rotateSecret(String encodedData, byte[] newSalt) {
        this.secretData = encodedData;
        this.salt = newSalt;
    }

    public void changePriority(int newPriority) {
        this.priority = newPriority;
    }

    public boolean belongTo(String userId) {
        return this.userId.equals(userId);
    }

    public static Credential create(String secretData, String type, String credentialData, byte[] salt, String userId) {
        Credential credential = new Credential();
        credential.userId = userId;
        credential.type = type;
        credential.secretData = secretData;
        credential.salt = salt;
        credential.credentialData = credentialData;
        credential.priority = 0;
        credential.version = 1;
        credential.userId = userId;
        return credential;
    }

    public static Credential create(String id, String secretData, String type, String credentialData, byte[] salt, String userId, int version) {
        Credential credential = new Credential();
        credential.id = id;
        credential.userId = userId;
        credential.type = type;
        credential.secretData = secretData;
        credential.salt = salt;
        credential.credentialData = credentialData;
        credential.priority = 0;
        credential.userId = userId;
        credential.version = version;
        return credential;
    }
}

