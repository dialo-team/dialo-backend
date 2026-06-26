package com.fit.se.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
public class JwtTokenProvider {
    private static final String TOKEN_TYPE = "token_type";
    private static final String ACCESS_TOKEN = "ACCESS_TOKEN";

    private final PublicKey publicKey;

    public JwtTokenProvider(@Value("${security.jwt.public-key:classpath:certs/public.pem}") Resource publicKeyResource) {
        this.publicKey = loadPublicKey(publicKeyResource);
    }

    public String extractAndValidateAccessSubject(String token) {
        String subject = extractSubject(token);
        String tokenType = extractTokenType(token);
        if (!ACCESS_TOKEN.equals(tokenType)) {
            throw new IllegalArgumentException("STOMP chi chap nhan access token");
        }
        return subject;
    }

    public String extractSubject(String token) {
        String subject = extractClaims(token).getSubject();
        if (subject == null || subject.isBlank()) {
            throw new IllegalArgumentException("Token khong chua subject hop le");
        }
        return subject;
    }

    public String extractTokenType(String token) {
        String tokenType = extractClaims(token).get(TOKEN_TYPE, String.class);
        if (tokenType == null || tokenType.isBlank()) {
            throw new IllegalArgumentException("Token khong chua token_type hop le");
        }
        return tokenType;
    }

    private Claims extractClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(publicKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception ex) {
            throw new IllegalArgumentException("Access token khong hop le", ex);
        }
    }

    private PublicKey loadPublicKey(Resource publicKeyResource) {
        try (InputStream inputStream = publicKeyResource.getInputStream()) {
            String key = new String(inputStream.readAllBytes())
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s", "");

            byte[] decoded = Base64.getDecoder().decode(key);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);
            return KeyFactory.getInstance("RSA").generatePublic(keySpec);
        } catch (Exception ex) {
            throw new IllegalStateException("Khong tai duoc JWT public key cho messaging", ex);
        }
    }
}
