package com.fit.se.controller;

import com.fit.se.dto.request.RefreshRequest;
import com.fit.se.dto.request.SignInRequest;
import com.fit.se.dto.request.SignUpRequest;
import com.fit.se.dto.response.ApiResponse;
import com.fit.se.service.AccountService;
import com.fit.se.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authService;
    private final AccountService accountService;

    @PostMapping("/signin")
    public ApiResponse<?> signIn(@RequestBody SignInRequest request) {
        return ApiResponse.builder()
                .status(200)
                .data(authService.login(request))
                .build();
    }

    @PostMapping("/signup")
    public ApiResponse<?> signUp(@RequestBody SignUpRequest request) {
        accountService.create(request);
        return ApiResponse.builder()
                .status(201)
                .message("account is created")
                .build();
    }

    @PostMapping("/signout")
    public ApiResponse<?> signOut(@RequestBody RefreshRequest request) {
        authService.signOut(request);
        return ApiResponse.builder()
                .status(201)
                .message("account is logout")
                .build();
    }

    @PostMapping("/refresh")
    public ApiResponse<?> refreshToken(@RequestBody RefreshRequest request) {
        return ApiResponse.builder()
                .status(200)
                .data(authService.refreshToken(request))
                .build();
    }

}
