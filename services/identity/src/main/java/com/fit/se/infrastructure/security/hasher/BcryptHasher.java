package com.fit.se.infrastructure.security.hasher;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

@Component
public class BcryptHasher implements HasherFactory {
    @Override
    public String generateSalt() {
        return BCrypt.gensalt();
    }

    @Override
    public String hash(String rawData, String salt) {
        return BCrypt.hashpw(rawData, salt);
    }

    @Override
    public boolean matches(String rawData, String hashData, String salt) {
        return BCrypt.checkpw(rawData, hashData);
    }
}
