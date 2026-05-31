package com.fit.se.apigateway.infrastructure.security.jwt;

import com.fit.se.apigateway.infrastructure.security.token.TokenProvider;
import com.fit.se.apigateway.infrastructure.security.token.TokenPurpose;
import com.fit.se.apigateway.infrastructure.security.token.TokenStatus;
import com.fit.se.apigateway.infrastructure.security.token.TokenValidationResult;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.PublicKey;
import java.util.Date;
@Component
@RequiredArgsConstructor
public class JWTProvider implements TokenProvider {
    private static final String TOKEN_TYPE = "token_type";
    private static final String JTI = "jti";
    private final PublicKey publicKey;


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
            case "RESET_TOKEN" -> TokenPurpose.RESET;
            default -> throw new RuntimeException("Unknown token type");
        };
    }

    private boolean isTokenExpired(final String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    public Claims extractClaims(String token) {
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
}
