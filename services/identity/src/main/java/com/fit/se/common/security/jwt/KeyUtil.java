package com.fit.se.common.security.jwt;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
public class KeyUtil {
    private KeyUtil() {}

    @Bean
    public PrivateKey loadPrivateKey() {
        try {
            final String key = readKeyFromResource("certs/private.pem")
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");

            final byte[] decoded = Base64.getDecoder().decode(key);
            final PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
            return KeyFactory.getInstance("RSA").generatePrivate(keySpec);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    public PublicKey loadPublicKey() throws Exception {
        try {
            final String key = readKeyFromResource("certs/public.pem")
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s", "");

            final byte[] decoded = Base64.getDecoder().decode(key);
            final X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);
            return KeyFactory.getInstance("RSA").generatePublic(keySpec);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String readKeyFromResource(final String path) throws Exception {
        try(final InputStream is = KeyUtil.class.getClassLoader().getResourceAsStream(path)) {
            if(is == null) throw new IllegalAccessException("Key not found: " + path);
            return new String(is.readAllBytes());
        }
    }
}


