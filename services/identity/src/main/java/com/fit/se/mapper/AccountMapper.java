package com.fit.se.mapper;

import com.fit.se.dto.request.SignUpRequest;
import com.fit.se.model.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountMapper {
    private final PasswordEncoder passwordEncoder;

    public Account toAccount(SignUpRequest request) {
        return Account.builder()
                .phone(request.phone())
                .password(passwordEncoder.encode(request.password()))
                .enabled(false)
                .locked(false)
                .build();
    }
}
