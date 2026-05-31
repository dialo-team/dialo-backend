package com.fit.se.session.domain;

import java.util.List;
import java.util.Optional;

public interface ClientSessionRepository {
    public String save(ClientSession client);

    ClientSession findById(String targetSessId);

    List<ClientSession> findByAccount(String accountId, SessionState state);

    void updateState(String sessId, SessionState sessionState);

    List<ClientSession> findByAccountWithout(String accountId, SessionState state);
}

