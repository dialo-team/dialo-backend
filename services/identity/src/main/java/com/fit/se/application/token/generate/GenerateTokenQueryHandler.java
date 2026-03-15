package com.fit.se.application.token.generate;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GenerateTokenQueryHandler {
    private final AuthenticationManager authManager;

    public GenerateTokenResult execute(GenerateTokenQuery query) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(query.phone(), query.password())
        );

        System.out.println("Agent: " + query.deviceName() + "\s| ip: " + query.ipAddress() );

        // handle logic

        return null;
    }
}
