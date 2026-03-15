package com.fit.se.domain.device;

import lombok.Builder;

import java.time.Instant;

@Builder
public class Device {
    private String id;

    private String name;
    private String ipAddress;
    private String loginMethod;

    private Instant lastLoginAt;
    private Instant lastActiveAt;

    private String accId;
}
