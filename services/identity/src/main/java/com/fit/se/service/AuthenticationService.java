package com.fit.se.service;

import com.fit.se.dto.request.RefreshRequest;
import com.fit.se.dto.request.SignInRequest;
import com.fit.se.dto.response.TokenResponse;

public interface AuthenticationService {
    public TokenResponse login(SignInRequest request);

    public void signOut(RefreshRequest request);

    public TokenResponse refreshToken(RefreshRequest request);
}
