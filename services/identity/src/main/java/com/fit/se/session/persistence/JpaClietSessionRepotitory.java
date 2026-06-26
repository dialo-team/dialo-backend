package com.fit.se.session.persistence;

import com.fit.se.session.domain.ClientSession;
import com.fit.se.session.domain.ClientSessionRepository;
import com.fit.se.session.domain.SessionState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class JpaClietSessionRepotitory implements ClientSessionRepository {
    private final SpringDataClientSessionRepository springDataClientSession;
    private final ClientSessionMapper clientSessionMapper;

    @Override
    public String save(ClientSession client) {
        String id = springDataClientSession.save(ClientSessionEntity.builder()
                .clientId(client.getClientId())
                .userId(client.getUserId())
                .deviceName(client.getDeviceName())
                .agentName(client.getAgentName())
                .signInDate(LocalDate.now())
                .method(client.getMethod())
                .status(client.getState().name())
                .lastActiveDate(LocalDate.now())
                .address(client.getAddress())
                .ipAddress(client.getIpAddress())
                .build()).getClientId();
        return id;
    }

    @Override
    public ClientSession findById(String targetSessId) {
        return clientSessionMapper.toDomain(springDataClientSession.findById(targetSessId).orElseThrow()) ;
    }

    @Override
    public List<ClientSession> findByAccount(String accountId, SessionState state) {
        return springDataClientSession.findByUserIdAndStatus(accountId, state.name()).stream()
                .map(clientSessionMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void updateState(String sessId, SessionState sessionState) {
//        springDataClientSession.save();
    }

    @Override
    public List<ClientSession> findByAccountWithout(String accountId, SessionState state) {
        return springDataClientSession.findByUserIdAndStatusNot(accountId, state.name()).stream()
                .map(clientSessionMapper::toDomain)
                .collect(Collectors.toList());
    }
}

