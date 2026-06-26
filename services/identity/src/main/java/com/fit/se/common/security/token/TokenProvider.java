package com.fit.se.common.security.token;

import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Component;

@Component
public interface TokenProvider {
    String generate(TokenPurpose purpose, String subject);
    TokenValidationResult validate(TokenPurpose purpose, String subject, String token);
    String extractSubject(String token);
    String extractJti(String token);
    TokenPurpose extractPurpose(String token);
    Claims extractClaims(String token);
}

