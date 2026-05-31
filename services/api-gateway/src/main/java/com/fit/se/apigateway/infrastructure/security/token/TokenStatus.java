package com.fit.se.apigateway.infrastructure.security.token;

public enum TokenStatus {
    ACTIVE,
    EXPIRED,
    REVOKED,
    INVALID,
    USED
}
