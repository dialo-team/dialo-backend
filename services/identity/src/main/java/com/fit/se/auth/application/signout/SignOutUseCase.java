package com.fit.se.auth.application.signout;

import com.fit.se.common.exception.errors.SessionNotFoundException;
import com.fit.se.common.exception.errors.SessionOwnershipException;
import com.fit.se.common.exception.tokens.RefreshTokenInvalidException;
import com.fit.se.common.exception.tokens.RefreshTokenNotFoundException;
import com.fit.se.session.domain.ClientSession;
import com.fit.se.session.domain.ClientSessionRepository;
import com.fit.se.session.domain.SessionState;
import com.fit.se.common.cache.CacheStore;
import com.fit.se.common.security.token.TokenProvider;
import com.fit.se.common.security.token.TokenPurpose;
import com.fit.se.common.security.token.TokenStore;
import com.fit.se.common.websocket.AuthEventPublisher;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
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
        Claims claims = extractRefreshClaims(refreshToken);
        String subject = claims.getSubject();
        String jti = claims.getId();

        if (!accountId.equals(subject) || !jti.equals(sessId)) {
            throw new RefreshTokenInvalidException();
        }
        if (!tokenStore.exists(accountId, jti)) {
            throw new RefreshTokenNotFoundException();
        }
        if (!tokenStore.matches(refreshToken, accountId, jti)) {
            throw new RefreshTokenInvalidException();
        }

        ClientSession session = findSession(sessId);
        if (!accountId.equals(session.getUserId())) {
            throw new SessionOwnershipException();
        }

        tokenStore.revoke(accountId, jti);
        session.changeState(SessionState.LOGGED_OUT);
        clientSessionRepo.save(session);
        authEventPublisher.publishForceLogoutBySession(sessId, "SIGNED_OUT");
    }

    public void executeAll(String refreshToken) {
        Claims claims = extractRefreshClaims(refreshToken);
        String subject = claims.getSubject();
        String currentJti = claims.getId();
        String deviceSetKey = "token:user:" + subject + ":refresh-tokens";
        Set<String> jtis = cacheStore.getSetMembers(deviceSetKey);

        if (jtis == null || jtis.isEmpty()) {
            throw new RefreshTokenNotFoundException();
        }
        if (!jtis.contains(currentJti)) {
            throw new RefreshTokenNotFoundException();
        }
        if (!tokenStore.matches(refreshToken, subject, currentJti)) {
            throw new RefreshTokenInvalidException();
        }

        for (String jti : jtis) {
            tokenStore.revoke(subject, jti);
        }

        clientSessionRepo.findByAccount(subject, SessionState.ACTIVE).forEach(session -> {
            session.changeState(SessionState.REVOKED);
            clientSessionRepo.save(session);
        });
        cacheStore.delete(deviceSetKey);
    }

    public void executeClient(String currentUserId, String targetSessId) {
        ClientSession targetSession = findSession(targetSessId);
        if (!currentUserId.equals(targetSession.getUserId())) {
            throw new SessionOwnershipException();
        }

        tokenStore.revoke(currentUserId, targetSessId);
        targetSession.changeState(SessionState.REVOKED);
        clientSessionRepo.save(targetSession);
        authEventPublisher.publishForceLogoutBySession(targetSessId, "REVOKED_BY_USER");
    }

    private Claims extractRefreshClaims(String refreshToken) {
        try {
            Claims claims = tokenProvider.extractClaims(refreshToken);
            if (tokenProvider.extractPurpose(refreshToken) != TokenPurpose.REFRESH) {
                throw new RefreshTokenInvalidException();
            }
            return claims;
        } catch (RefreshTokenInvalidException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RefreshTokenInvalidException();
        }
    }

    private ClientSession findSession(String sessId) {
        try {
            return clientSessionRepo.findById(sessId);
        } catch (NoSuchElementException ex) {
            throw new SessionNotFoundException();
        }
    }
}

