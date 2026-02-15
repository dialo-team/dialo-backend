package com.fit.se.identity.auth;

import com.fit.se.identity.auth.request.AuthenticationRequest;
import com.fit.se.identity.auth.request.RefreshRequest;
import com.fit.se.identity.auth.request.RegistrationRequest;
import com.fit.se.identity.auth.response.AuthenticationResponse;

public interface AuthenticationService {
    AuthenticationResponse login(AuthenticationRequest request);

    void register(RegistrationRequest request) throws Exception;

    AuthenticationResponse refreshToken(RefreshRequest request);
}
