package com.fit.se.infrastructure.security.hasher;

public interface HasherFactory {
    String generateSalt();
    String hash(String rawData, String salt);
    boolean matches(String rawData, String hashData, String salt);
}
