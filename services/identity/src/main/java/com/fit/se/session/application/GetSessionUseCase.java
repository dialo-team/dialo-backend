package com.fit.se.session.application;

import com.fit.se.session.domain.ClientSession;
import com.fit.se.session.domain.ClientSessionRepository;
import com.fit.se.session.domain.SessionState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetSessionUseCase {
    private final ClientSessionRepository clientSessionRepo;

    public List<ClientSession> execute(String accountId, SessionState state) {
        return clientSessionRepo.findByAccount(accountId, state);
    }

    public List<ClientSession> executeWithout(String accountId, SessionState state) {
        return clientSessionRepo.findByAccountWithout(accountId, state);
    }
}

