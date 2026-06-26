package com.fit.se.auth.infrastructure.otp;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class SecureNumericCodeGenerator implements CodeGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    public String generate(int length) {
        int bound = (int) Math.pow(10, length);
        int number = RANDOM.nextInt(bound);
        return String.format("%0" + length + "d", number);
    }
}

