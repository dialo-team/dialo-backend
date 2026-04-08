package com.fit.se.infrastructure.security.jwt;

import com.fit.se.infrastructure.security.token.TokenProvider;
import com.fit.se.infrastructure.security.token.TokenPurpose;
import com.fit.se.infrastructure.security.token.TokenStatus;
import com.fit.se.infrastructure.security.token.TokenValidationResult;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JWTProvider implements TokenProvider {
    private static final String TOKEN_TYPE = "token_type";
    private static final String JTI = "jti";
    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    @Value("${app.security.jwt.access-token-expiration}")
    private long accessTokenExpiration;
    @Value("${app.security.jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;
    private long resetTokenExpiration = 300000;

    @Override
    public String generate(TokenPurpose purpose, String subject) {
        String jti = UUID.randomUUID().toString();
        return switch (purpose) {
            case ACCESS -> generateAccessToken(subject, jti);
            case REFRESH -> generateRefreshToken(subject, jti);
            case RESET -> generateResetToken(subject, jti);
        };
    }

    @Override
    public TokenValidationResult validate(TokenPurpose ePurpose, String eSubject, String token) {
        try {
            String aSubject = extractSubject(token);
            TokenPurpose aPurpose = extractPurpose(token);

            if(!aSubject.equals(eSubject)) {
                return TokenValidationResult.invalid(TokenStatus.INVALID, "Subject mismatch");
            }

            if(!aPurpose.equals(ePurpose)) {
                return TokenValidationResult.invalid(TokenStatus.INVALID, "Purpose mismatch");
            }

            return TokenValidationResult.valid(aSubject, aPurpose);
        } catch (Exception e) {
            return TokenValidationResult.invalid(TokenStatus.INVALID, "Invalid token");
        }
    }

    @Override
    public String extractSubject(String token) {
        return extractClaims(token).getSubject();
    }

    @Override
    public String extractJti(String token) {
        return extractClaims(token).get(JTI, String.class);
    }

    @Override
    public TokenPurpose extractPurpose(String token) {
        String tokenType = extractClaims(token).get(TOKEN_TYPE, String.class);

        return switch (tokenType) {
            case "ACCESS_TOKEN" -> TokenPurpose.ACCESS;
            case "REFRESH_TOKEN" -> TokenPurpose.REFRESH;
            default -> throw new RuntimeException("Unknown token type");
        };
    }

    private String generateAccessToken(String subject, String jti) {
        final Map<String, Object> claims = Map.of(
                TOKEN_TYPE, "ACCESS_TOKEN",
                JTI, jti
        );
        return buildToken(subject, claims, this.accessTokenExpiration );
    }

    private String generateRefreshToken(String subject, String jti) {
        final Map<String, Object> claims = Map.of(
                TOKEN_TYPE, "REFRESH_TOKEN",
                JTI, jti
        );
        return buildToken(subject, claims, this.refreshTokenExpiration );
    }

    private String generateResetToken(String subject, String jti) {
        final Map<String, Object> claims = Map.of(
                TOKEN_TYPE, "RESET",
                JTI, jti
        );
        return buildToken(subject, claims, this.resetTokenExpiration);
    }

    private boolean isTokenExpired(final String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    public Claims extractClaims(String token) {
        System.out.println("token: " + token);
        try {
            return Jwts.parser()
                    .verifyWith(publicKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            throw new RuntimeException("Invalid token", e);
        }
    }

    private String buildToken(final String userName, final Map<String, Object> claims, final long expiration) {
        return Jwts.builder()
                .claims(claims)
                .subject(userName)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(privateKey)
                .compact();
    }
}
