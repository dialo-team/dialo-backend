package com.fit.se.auth.infrastructure.otp;

public interface CodeGenerator {
    String generate(int length);
}

