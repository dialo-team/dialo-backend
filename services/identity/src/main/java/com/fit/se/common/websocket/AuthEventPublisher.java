package com.fit.se.common.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthEventPublisher {
    private final SimpMessagingTemplate messagingTemplate;

    public void publishQrApproved(String challengeId) {
        Object event = Map.of(
                "type", "QR_APPROVED",
                "challengeId", challengeId,
                "status", "APPROVED",
                "at", Instant.now().toString()
        );
        messagingTemplate.convertAndSend("/topic/qr." + challengeId, event);
    }

    public void publishQrExpired(String challengeId) {
        Object event = Map.of(
                "type", "QR_EXPIRED",
                "challengeId", challengeId,
                "status", "EXPIRED",
                "at", Instant.now().toString()
        );
        messagingTemplate.convertAndSend("/topic/qr." + challengeId, event);
    }

    public void publishForceLogoutBySession(String sessionId, String reason) {
        Object event = Map.of(
                "type", "FORCE_LOGOUT",
                "sessionId", sessionId,
                "reason", reason,
                "at", Instant.now().toString()
        );
        messagingTemplate.convertAndSend("/topic/session." + sessionId, event);
    }

    public void publishForceLogoutByUser(String userId, String reason) {
        Object event = Map.of(
                "type", "FORCE_LOGOUT_ALL",
                "userId", userId,
                "reason", reason,
                "at", Instant.now().toString()
        );
        messagingTemplate.convertAndSend("/topic/user." + userId + ".auth-events", event);
    }
}

