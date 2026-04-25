package com.fit.se.application.signout;

import com.fit.se.api.exception.tokens.RefreshTokenNotFoundException;
import com.fit.se.domain.session.ClientSession;
import com.fit.se.domain.session.ClientSessionRepository;
import com.fit.se.domain.session.SessionState;
import com.fit.se.infrastructure.cache.CacheStore;
import com.fit.se.infrastructure.security.token.TokenProvider;
import com.fit.se.infrastructure.security.token.TokenStore;
import com.fit.se.infrastructure.websocket.AuthEventPublisher;
import io.jsonwebtoken.Claims;
import jakarta.mail.Session;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class SignOutUseCase {
    private final TokenProvider tokenProvider;
    private final CacheStore cacheStore;
    private final TokenStore tokenStore;
    private final AuthEventPublisher authEventPublisher;
    private final ClientSessionRepository clientSessionRepo;

    public void execute(String accountId, String refreshToken, String ipAddress, String sessId) {
        String jti = tokenProvider.extractJti(refreshToken);
        if(!tokenStore.matches(refreshToken, accountId, jti)) {
            throw new RuntimeException("Invalid refresh token");
        }
        tokenStore.revoke(accountId, jti);
        ClientSession session = clientSessionRepo.findById(sessId);
        session.changeState(SessionState.LOGGED_OUT);
        clientSessionRepo.save(session);
        authEventPublisher.publishForceLogoutBySession(sessId, "SIGNED_OUT");
    }

    public void executeAll(String refreshToken) {
        String subject = tokenProvider.extractSubject(refreshToken);
        String deviceSetKey = "token:user:" + subject + ":refresh-tokens";
        Set<String> jtis = cacheStore.getSetMembers(deviceSetKey);
        if (jtis == null || jtis.isEmpty()) {
            throw new RefreshTokenNotFoundException();
        }
        boolean matched = false;
        for (String jti : jtis) {
            if(tokenStore.matches(refreshToken, subject, jti)) {
                matched = true;
            }
            tokenStore.revoke(subject, jti);
        }
        clientSessionRepo.findByAccount(subject, SessionState.ACTIVE).forEach(session -> {
            session.changeState(SessionState.REVOKED);
            clientSessionRepo.save(session);
        });
        cacheStore.delete(deviceSetKey);
        if (!matched) {
            throw new RuntimeException("Invalid refresh token");
        }
    }

    public void executeClient(String currentUserId, String targetSessId) {
        ClientSession targetSession = clientSessionRepo.findById(targetSessId);
        tokenStore.revoke(currentUserId, targetSessId);
        if (!currentUserId.equals(targetSession.getUserId())) {
            throw new RuntimeException("You cannot sign out another user's session");
        }
        targetSession.changeState(SessionState.REVOKED);
        clientSessionRepo.save(targetSession);
        authEventPublisher.publishForceLogoutBySession(targetSessId, "REVOKED_BY_USER");
    }
}
