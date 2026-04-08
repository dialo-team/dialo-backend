package com.fit.se.domain.conversation.valueobject;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Objects;

public record JoinToken(String value) {
    private static final SecureRandom RANDOM = new SecureRandom();

    public JoinToken {
        Objects.requireNonNull(value, "joinToken must not be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("joinToken must not be blank");
        }
        if (value.length() < 12 || value.length() > 128) {
            throw new IllegalArgumentException("joinToken length must be between 12 and 128");
        }
    }

    public static JoinToken randomToken() {
        byte[] bytes = new byte[24];
        RANDOM.nextBytes(bytes);
        return new JoinToken(Base64.getUrlEncoder().withoutPadding().encodeToString(bytes));
    }
}
