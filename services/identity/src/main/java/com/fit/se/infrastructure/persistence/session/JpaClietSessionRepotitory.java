package com.fit.se.infrastructure.persistence.session;

import com.fit.se.domain.session.ClientSession;
import com.fit.se.domain.session.ClientSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
@RequiredArgsConstructor
public class JpaClietSessionRepotitory implements ClientSessionRepository {
    private final SpringDataClientSessionRepository springDataClientSession;

    @Override
    public void save(ClientSession client) {
        springDataClientSession.save(ClientSessionEntity.builder()
                .userId(client.getUserId())
                .deviceName(client.getDeviceName())
                .signInDate(LocalDate.now())
                .method(client.getMethod())
                .lastActiveDate(LocalDate.now())
                .address(client.getAddress())
                .ipAddress(client.getIpAddress())
                .build());
    }
}
