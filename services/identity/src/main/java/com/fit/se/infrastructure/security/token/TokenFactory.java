package com.fit.se.infrastructure.security.token;

public interface TokenFactory {
    TokenProvider render(TokenFormat format);
}
