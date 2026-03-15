package com.fit.se.infrastructure.token;

import java.util.Map;

public interface JwtService {
    public String generateAccessToken(final String userName);
    public String generateRefreshToken(final String userName);
    public void storeRefreshToken(final String refreshToken, String accId, String deviceId, String deviceType);
    public String buildToken(final String userName, final Map<String, Object> claims, final long expiration);
    public boolean isTokenValid(final String token, final String expectUserName);
    public String extractUserName(final String token);
}
