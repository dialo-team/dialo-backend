package com.fit.se.domain.session;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Builder
@Getter
public class ClientSession {
    private String clientId;
    private String userId;

    private String deviceName;
    private String agentName;

    private String method;

    private LocalDate signInDate;
    private LocalDate lastActiveDate;
    private String address;
    private String ipAddress;
}
