package com.fit.se.application.signin;

import com.fit.se.api.exception.tokens.RefreshTokenNotFoundException;
import com.fit.se.application.token.generate.GenerateTokenResult;
import com.fit.se.domain.session.ClientSession;
import com.fit.se.domain.session.ClientSessionRepository;
import com.fit.se.domain.session.SessionState;
import com.fit.se.domain.user.Account;
import com.fit.se.domain.user.AccountRepository;
import com.fit.se.infrastructure.cache.CacheStore;
import com.fit.se.infrastructure.security.token.TokenProvider;
import com.fit.se.infrastructure.security.token.TokenPurpose;
import com.fit.se.infrastructure.security.token.TokenStore;
import com.fit.se.infrastructure.websocket.AuthEventPublisher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SignInUseCase {
    private final AuthenticationManager authManager;
    private final AccountRepository accountRepo;
    private final TokenProvider tokenProvider;
    private final TokenStore tokenStore;
    private final UserAgentAnalyzer analyzer;
    private final ClientSessionRepository clientSessionRepo;
    private final CacheStore cacheStore;
    private final AuthEventPublisher authEventPublisher;

    private static final long QR_CHALLENGE_EXPIRE_MILLIS = 2 * 60 * 1000;

    public GenerateTokenResult execute(String phone, String password, HttpServletRequest http) {
        Account storedAccount = authenticateByPassword(phone, password);
        return issueSession(storedAccount, http, "PASSWORD");
    }

    public QRChallengeResult generateQR(HttpServletRequest http) {
        RequestClientInfo clientInfo = extractClientInfo(http);

        String challengeId = UUID.randomUUID().toString();
        String challengeKey = buildChallengeKey(challengeId);

        Instant createdAt = Instant.now();
        Instant expiredAt = createdAt.plusMillis(QR_CHALLENGE_EXPIRE_MILLIS);

        Map<String, String> challenge = new HashMap<>();
        challenge.put("status", "PENDING");
        challenge.put("deviceIp", clientInfo.ipAddress());
        challenge.put("deviceName", buildAgentLabel(clientInfo));
        challenge.put("createdAt", createdAt.toString());
        challenge.put("expiredAt", expiredAt.toString());
        challenge.put("approvedBy", "");

        cacheStore.putHash(challengeKey, challenge);
        cacheStore.expire(challengeKey, QR_CHALLENGE_EXPIRE_MILLIS);

        return QRChallengeResult.builder()
                .challengeId(challengeId)
                .content(challengeId)
                .expiredIn(QR_CHALLENGE_EXPIRE_MILLIS)
                .build();
    }

    public void approveQR(String challengeId, String userId) {
        String challengeKey = buildChallengeKey(challengeId);

        Map<Object, Object> challenge = cacheStore.getHash(challengeKey);
        if (challenge == null || challenge.isEmpty()) {
            throw new RuntimeException("QR challenge not found");
        }

        String status = String.valueOf(challenge.get("status"));
        if (!"PENDING".equals(status)) {
            throw new RuntimeException("QR challenge is not pending");
        }

        Map<String, String> updated = new HashMap<>();
        challenge.forEach((k, v) -> updated.put(String.valueOf(k), v == null ? "" : String.valueOf(v)));

        updated.put("status", "APPROVED");
        updated.put("approvedBy", userId);
        updated.put("approvedAt", Instant.now().toString());

        cacheStore.putHash(challengeKey, updated);

        authEventPublisher.publishQrApproved(challengeId);
    }

    public GenerateTokenResult exchangeQR(String challengeId, HttpServletRequest http) {
        String challengeKey = buildChallengeKey(challengeId);

        Map<Object, Object> challenge = cacheStore.getHash(challengeKey);
        if (challenge == null || challenge.isEmpty()) {
            throw new RuntimeException("QR challenge not found");
        }

        String status = String.valueOf(challenge.get("status"));
        if (!"APPROVED".equals(status)) {
            throw new RuntimeException("QR challenge has not been approved");
        }

        String userId = String.valueOf(challenge.get("approvedBy"));
        if (userId == null || userId.isBlank()) {
            throw new RuntimeException("QR challenge approved user not found");
        }

        String accessToken = tokenProvider.generate(TokenPurpose.ACCESS, userId);
        String refreshToken = tokenProvider.generate(TokenPurpose.REFRESH, userId);

        RequestClientInfo clientInfo = extractClientInfo(http);

        tokenStore.store(refreshToken, userId, tokenProvider.extractJti(refreshToken), clientInfo.agentName);

        clientSessionRepo.save(ClientSession.builder()
                .userId(userId)
                .deviceName(clientInfo.deviceClass())
                .agentName(clientInfo.agentName())
                .address("")
                .method("QR")
                .ipAddress(clientInfo.ipAddress())
                .build());

        Map<String, String> updated = new HashMap<>();
        challenge.forEach((k, v) -> updated.put(String.valueOf(k), v == null ? "" : String.valueOf(v)));

        updated.put("status", "EXCHANGED");
        updated.put("exchangedAt", Instant.now().toString());

        cacheStore.putHash(challengeKey, updated);
        cacheStore.expire(challengeKey, 30_000);

        return GenerateTokenResult.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .build();
    }

    private String buildChallengeKey(String challengeId) {
        return "auth:qr:challenge:" + challengeId;
    }

    private RequestClientInfo extractClientInfo(HttpServletRequest http) {
        String ua = http.getHeader("User-Agent");

        String ip = http.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isBlank() && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        if (ip == null || ip.isBlank()) {
            ip = http.getRemoteAddr();
        }

        UserAgent agent = analyzer.parse(ua == null ? "" : ua);

        return new RequestClientInfo(
                ip,
                safe(agent.getValue("DeviceClass")),
                safe(agent.getValue("AgentName")),
                safe(agent.getValue("AgentVersion"))
        );
    }

    private String buildAgentLabel(RequestClientInfo clientInfo) {
        StringBuilder sb = new StringBuilder();

        if (!clientInfo.deviceClass().isBlank()) {
            sb.append(clientInfo.deviceClass());
        }
        if (!clientInfo.agentName().isBlank()) {
            if (sb.length() > 0) {
                sb.append(" - ");
            }
            sb.append(clientInfo.agentName());
        }
        if (!clientInfo.agentVersion().isBlank()) {
            sb.append(" ").append(clientInfo.agentVersion());
        }

        return sb.length() == 0 ? "Unknown Device" : sb.toString();
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }

    private record RequestClientInfo(
            String ipAddress,
            String deviceClass,
            String agentName,
            String agentVersion
    ) {}

    private GenerateTokenResult issueSession(Account storedAccount, HttpServletRequest http, String method) {
        String ua = http.getHeader("User-Agent");
        String ip = http.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) {
            ip = http.getRemoteAddr();
        }

        UserAgent agent = analyzer.parse(ua == null ? "" : ua);
        String currentDevice = agent.getValue("AgentName");

        String deviceSetKey = "token:user:" + storedAccount.getId() + ":refresh-tokens";
        Set<String> jtis = cacheStore.getSetMembers(deviceSetKey);

        if (jtis != null && !jtis.isEmpty()) {
            List<String> matchedJtis = jtis.stream()
                    .filter(jti -> {
                        String tokenKey = "token:refresh:" + storedAccount.getId() + ":" + jti;
                        Map<Object, Object> tokenData = cacheStore.getHash(tokenKey);

                        if (tokenData == null || tokenData.isEmpty()) {
                            return false;
                        }

                        String storedDevice = String.valueOf(tokenData.get("device"));
                        return currentDevice.equals(storedDevice);
                    })
                    .toList();

            matchedJtis.forEach(jti -> tokenStore.revoke(storedAccount.getId(), jti));
        }

        String accessToken = tokenProvider.generate(TokenPurpose.ACCESS, storedAccount.getId());
        String refreshToken = tokenProvider.generate(TokenPurpose.REFRESH, storedAccount.getId());

        tokenStore.store(
                refreshToken,
                storedAccount.getId(),
                tokenProvider.extractJti(refreshToken),
                agent.getValue("AgentName")
        );

        String sessId = clientSessionRepo.save(ClientSession.builder()
                .clientId(tokenProvider.extractJti(refreshToken))
                .userId(storedAccount.getId())
                .deviceName(agent.getValue("DeviceClass"))
                .agentName(agent.getValue("AgentName"))
                .address("")
                .state(SessionState.ACTIVE)
                .method(method)
                .ipAddress(ip)
                .build());

        return GenerateTokenResult.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .sessId(sessId)
                .build();
    }

    private Account authenticateByPassword(String phone, String password) {
        Account storedAccount = accountRepo.findByPhone(phone)
                .orElseThrow(() -> new RuntimeException(""));
        boolean exists = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(storedAccount.getId(), password)
        ).isAuthenticated();

        if(!exists) {
            throw new RuntimeException("Đăng nhập thất bại! Kiểm tra lại số điện thoại hoặc mật khẩu.");
        }

        if(storedAccount.isLocked()) {
            throw new RuntimeException("Tài khoản đã bị khóa");
        }

        return storedAccount;
    }
}
