package com.fit.se.common.security.hasher;

public interface HasherFactory {
    String generateSalt();
    String hash(String rawData, String salt);
    boolean matches(String rawData, String hashData, String salt);
}

