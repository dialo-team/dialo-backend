package com.fit.se.common.security.token;

public interface TokenFactory {
    TokenProvider render(TokenFormat format);
}

