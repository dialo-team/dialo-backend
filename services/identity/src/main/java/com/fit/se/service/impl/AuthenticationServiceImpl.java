package com.fit.se.service.impl;

import com.fit.se.dto.request.RefreshRequest;
import com.fit.se.dto.request.SignInRequest;
import com.fit.se.dto.response.TokenResponse;
import com.fit.se.exception.tokens.RefreshTokenNotFoundException;
import com.fit.se.model.Account;
import com.fit.se.model.SecurityUser;
import com.fit.se.service.AuthenticationService;
import com.fit.se.service.JwtService;
import com.fit.se.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final RedisService redisService;

    @Override
    public TokenResponse login(SignInRequest request) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.phone(), request.password())
        );

        final String tokenType = "Bearer";
        SecurityUser securityUser = (SecurityUser) auth.getPrincipal();
        Account account = Objects.requireNonNull(securityUser).getAccount();
        String userId = account.getId();

        String accessToken = jwtService.generateAccessToken(userId);
        String refreshToken = jwtService.generateRefreshToken(userId);

        jwtService.storeRefreshToken(refreshToken, account.getId(), request.deviceId(), request.deviceType());

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .deviceId(request.deviceId())
                .deviceType(request.deviceType())
                .tokenType(tokenType)
                .build();
    }

    @Override
    public void signOut(RefreshRequest request) {
        String userId = jwtService.extractUserName(request.refreshToken());
        String storedRefreshToken = redisService.get(userId)
                .orElseThrow(RefreshTokenNotFoundException::new);
        if (!storedRefreshToken.equals(request.refreshToken())) {
            throw new RuntimeException("Invalid refresh token");
        }
        redisService.delete(userId);
    }

    @Override
    public TokenResponse refreshToken(RefreshRequest request) {
        String phone = jwtService.extractUserName(request.refreshToken());
        String refreshToken = redisService.get(phone).orElseThrow(RefreshTokenNotFoundException::new);
        redisService.delete(phone);

        final String newAccessToken = jwtService.generateAccessToken(refreshToken);
        final String tokenType = "Bearer";
        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(request.refreshToken())
                .tokenType(tokenType)
                .build();
    }
}
