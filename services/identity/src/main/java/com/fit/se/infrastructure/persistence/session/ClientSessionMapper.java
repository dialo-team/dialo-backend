package com.fit.se.infrastructure.persistence.session;

import com.fit.se.domain.session.ClientSession;
import com.fit.se.domain.session.SessionState;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ClientSessionMapper {

    ClientSession toDomain(ClientSessionEntity entity) {
        return ClientSession.builder()
                .clientId(entity.getClientId())
                .userId(entity.getUserId())
                .deviceName(entity.getDeviceName())
                .agentName(entity.getAgentName())
                .method(entity.getMethod())
                .state(SessionState.valueOf(entity.getStatus()))
                .signInDate(entity.getSignInDate())
                .lastActiveDate(entity.getLastActiveDate())
                .address(entity.getAddress())
                .ipAddress(entity.getIpAddress())
                .build();
    }
}
