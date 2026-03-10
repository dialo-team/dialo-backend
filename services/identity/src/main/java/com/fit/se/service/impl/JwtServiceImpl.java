package com.fit.se.service.impl;

import com.fit.se.dto.response.DeviceSessionResponse;
import com.fit.se.service.DeviceSessionService;
import com.fit.se.service.JwtService;
import com.fit.se.service.RedisService;
import com.fit.se.util.KeyUtil;
import com.fit.se.util.RefreshTokenHasher;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class JwtServiceImpl implements JwtService {
    public static final String TOKEN_TYPE = "token_type";
    private final PrivateKey privateKey;
    private final PublicKey publicKey;
    @Value("${app.security.jwt.access-token-expiration}")
    private long accessTokenExpiration;
    @Value("${app.security.jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;
    private final RedisService redisService;
    private final RefreshTokenHasher refreshTokenHasher;
    private final DeviceSessionService deviceSessionService;

    public JwtServiceImpl(
            RedisService redisService,
            RefreshTokenHasher refreshTokenHasher,
            DeviceSessionService deviceSessionService
    ) throws Exception {
        this.redisService = redisService;
        this.refreshTokenHasher = refreshTokenHasher;
        this.deviceSessionService = deviceSessionService;
        this.privateKey = KeyUtil.loadPrivateKey("certs/private.pem");
        this.publicKey = KeyUtil.loadPublicKey("certs/public.pem");
    }

    public String generateAccessToken(final String userName) {
        final Map<String, Object> claims = Map.of(TOKEN_TYPE, "ACCESS_TOKEN");
        return buildToken(userName, claims, this.accessTokenExpiration );
    }

    public String generateRefreshToken(final String userName) {
        final Map<String, Object> claims = Map.of(TOKEN_TYPE, "REFRESH_TOKEN");
        return buildToken(userName, claims, this.refreshTokenExpiration );
    }

    @Override
    public void storeRefreshToken(String refreshToken, String accId, String deviceId, String deviceType) {
        String deviceKey = "auth:refresh:" + accId + ":" + deviceId;
        String deviceSetKey = "auth:user:" + accId + ":devices";
        String refreshTokenHash = refreshTokenHasher.hash(refreshToken);

        List<DeviceSessionResponse> devices = deviceSessionService.getLoggedInDevices(accId);
        devices.stream()
                .filter(device -> device.deviceType().equals(deviceType))
                .forEach(device -> {
                    redisService.removeFromSet(deviceSetKey, device.deviceId());
                    redisService.delete("auth:refresh:" + accId + ":" + device.deviceId());
                });

        Instant now = Instant.now();
        redisService.addToSet(deviceSetKey, deviceId);
        redisService.putHash(deviceKey, Map.of(
                "refreshToken", refreshTokenHash,
                "deviceType", deviceType,
                "createdAt", now.toString(),
                "lastActiveAt", now.toString()
        ));
        redisService.expire(deviceKey, refreshTokenExpiration);
    }

    public String buildToken(final String userName, final Map<String, Object> claims, final long expiration) {
        return Jwts.builder()
                .claims(claims)
                .subject(userName)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(this.privateKey)
                .compact();
    }

    public boolean isTokenValid(final String token, final String expectUserName) {
        final String userName = extractUserName(token);
        return userName.equals(expectUserName) && !isTokenExpired(token);
    }

    public String extractUserName(final String token) {
        return extractClaims(token).getSubject();
    }

    private boolean isTokenExpired(final String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    private Claims extractClaims(final String token) {
        try {
            return Jwts.parser()
                    .verifyWith(this.publicKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            throw new RuntimeException("Invalid token", e);
        }
    }
}
