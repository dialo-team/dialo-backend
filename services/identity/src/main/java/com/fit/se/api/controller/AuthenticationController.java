package com.fit.se.api.controller;

import com.fit.se.api.dto.request.SignInRequest;
import com.fit.se.application.token.generate.GenerateTokenQuery;
import com.fit.se.application.token.generate.GenerateTokenQueryHandler;
import com.fit.se.application.token.refresh.RefreshTokenQuery;
import com.fit.se.application.token.refresh.RefreshTokenQueryHandler;
import com.fit.se.application.token.revoke.RevokeQuery;
import com.fit.se.application.token.revoke.RevokeTokenHandler;
import com.fit.se.application.user.command.signup.SignUpCommand;
import com.fit.se.application.user.command.signup.SignUpCommandHandler;
import com.fit.se.api.dto.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final SignUpCommandHandler signUpHandler;
    private final GenerateTokenQueryHandler generateTokenHandler;
    private final RevokeTokenHandler revokeTokenHandler;
    private final RefreshTokenQueryHandler refreshTokenHandler;

    @PostMapping("/signin")
    public ApiResponse<?> signIn(@RequestBody SignInRequest request,
                                 HttpServletRequest httpRequest) {
        String agent = httpRequest.getHeader("User-Agent");
        String ip = httpRequest.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) {
            ip = httpRequest.getRemoteAddr();
        }
        GenerateTokenQuery query = GenerateTokenQuery.builder()
                .phone(request.phone())
                .password(request.password())
                .deviceName(agent)
                .ipAddress(ip)
                .loginMethod("PASSWORD")
                .build();

        System.out.println("Agent: " + query.deviceName() + "\s| ip: " + query.ipAddress() );

        return ApiResponse.builder()
                .status(200)
                .data(generateTokenHandler.execute(query))
                .build();
    }

    @PostMapping("/signup")
    public ApiResponse<?> signUp(@RequestBody SignUpCommand request) {
        signUpHandler.execute(request);
        return ApiResponse.builder()
                .status(201)
                .message("account is created")
                .build();
    }

    @PostMapping("/signout")
    public ApiResponse<?> signOut(@RequestBody RevokeQuery request) {
        revokeTokenHandler.execute(request);
        return ApiResponse.builder()
                .status(201)
                .message("account is logout")
                .build();
    }

    @PostMapping("/refresh")
    public ApiResponse<?> refreshToken(@RequestBody RefreshTokenQuery request) {
        return ApiResponse.builder()
                .status(200)
                .data(refreshTokenHandler.execute(request))
                .build();
    }

}
