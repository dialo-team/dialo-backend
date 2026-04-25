package com.fit.se.apigateway.infrastructure.security.token;

public interface TokenFactory {
    TokenProvider render(TokenFormat format);
}
