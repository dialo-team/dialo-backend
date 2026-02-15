package com.fit.se.identity.auth;

import com.fit.se.identity.auth.request.AuthenticationRequest;
import com.fit.se.identity.auth.request.RefreshRequest;
import com.fit.se.identity.auth.request.RegistrationRequest;
import com.fit.se.identity.auth.response.AuthenticationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody final AuthenticationRequest request) {
        System.out.println("controller");
        return ResponseEntity.ok(this.authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody final RegistrationRequest request) throws Exception {
        this.authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponse> refresh(@RequestBody final RefreshRequest request) {
        return ResponseEntity.ok(this.authService.refreshToken(request));
    }
}
