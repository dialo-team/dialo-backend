package com.fit.se.identity.auth.impl;

import com.fit.se.identity.auth.AuthenticationService;
import com.fit.se.identity.auth.request.AuthenticationRequest;
import com.fit.se.identity.auth.request.RefreshRequest;
import com.fit.se.identity.auth.request.RegistrationRequest;
import com.fit.se.identity.auth.response.AuthenticationResponse;
import com.fit.se.identity.role.Role;
import com.fit.se.identity.role.RoleRepository;
import com.fit.se.identity.security.JwtService;
import com.fit.se.identity.security.RedisService;
import com.fit.se.identity.user.User;
import com.fit.se.identity.user.UserMapper;
import com.fit.se.identity.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RedisService redisService;

    @Override
    public AuthenticationResponse login(AuthenticationRequest request) {
        final Authentication auth = this.authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getPhone(),
                        request.getPassword()
                )
        );

        final User user = (User) auth.getPrincipal();
        final String accessToken = this.jwtService.generateAccessToken(user.getPhone());
        final String refreshToken = this.jwtService.generateRefreshToken(user.getPhone());
        final String tokenType = "Bearer";

        redisService.set(user.getPhone(), refreshToken);

        return AuthenticationResponse.builder()
                .acessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType(tokenType)
                .build();
    }

    @Override
    public void register(RegistrationRequest request) throws Exception {
        if(userRepo.existsUserByPhone(request.getPhone())) {
            throw new Exception("");
        }
        final Role userRole = this.roleRepo.findByName("ROLE_USER")
                .orElseThrow(() -> new EntityNotFoundException("Role user does not exist"));

        final User user = this.userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(true);
        user.setRoles(Set.of(userRole));
        this.userRepo.save(user);
    }

    @Override
    public AuthenticationResponse refreshToken(RefreshRequest request) {
        String phone = jwtService.extractUserName(request.getRefreshToken());
        String refreshToken = redisService.get(phone).orElseThrow(() -> new RuntimeException("Ko co refresh token nay"));
        redisService.delete(phone);

        final String newAccessToken = jwtService.generateAccessToken(refreshToken);
        final String tokenType = "Bearer";
        return AuthenticationResponse.builder()
                .acessToken(newAccessToken)
                .refreshToken(request.getRefreshToken())
                .tokenType(tokenType)
                .build();
    }
}
