package com.fit.se.application.session;

import com.fit.se.domain.session.ClientSession;
import com.fit.se.domain.session.ClientSessionRepository;
import com.fit.se.domain.session.SessionState;
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
