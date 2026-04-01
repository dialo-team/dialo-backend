package com.fit.se.application.token.generate;

import com.fit.se.domain.session.ClientSession;
import com.fit.se.domain.session.ClientSessionRepository;
import com.fit.se.infrastructure.cache.CacheStore;
import com.fit.se.infrastructure.external.otp.OtpService;
import com.fit.se.infrastructure.security.token.TokenProvider;
import com.fit.se.infrastructure.security.token.TokenPurpose;
import com.fit.se.infrastructure.security.token.TokenStore;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class VerifyOTPForSignInCommandHandler {
    private final OtpService otpService;
    private final TokenProvider tokenProvider;
    private final TokenStore tokenStore;
    private final CacheStore cache;
    private final UserAgentAnalyzer analyzer;
    private final ClientSessionRepository clientSessionRepo;

    public GenerateTokenResult execute(VerifyOTPForSignInCommand cmd) {
        boolean isValid = otpService.validate(cmd.phone(), cmd.otp());
        if (!isValid) {
            throw new RuntimeException("OTP không hợp lệ hoặc đã hết hạn");
        }
        String key = "signin-draft:" + cmd.phone();
        Map<Object, Object> draft = cache.getHash(key);
        String userId = (String) draft.get("subject");
        cache.delete(key);

        String accessToken = tokenProvider.generate(TokenPurpose.ACCESS, userId);
        String refreshToken = tokenProvider.generate(TokenPurpose.REFRESH, userId);
        tokenStore.store(refreshToken, userId, tokenProvider.extractJti(refreshToken));

        saveClientSession(cmd.httpRequest(), userId);

        return GenerateTokenResult.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .build();
    }

    private void saveClientSession(HttpServletRequest httpRequest, String userId) {
        String ua = httpRequest.getHeader("User-Agent");
        String ip = httpRequest.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) {
            ip = httpRequest.getRemoteAddr();
        }
        UserAgent agent = analyzer.parse(ua == null ? "" : ua);
        clientSessionRepo.save(ClientSession.builder()
                        .userId(userId)
                        .deviceName(agent.getValue("DeviceClass"))
                        .agentName(agent.getValue("AgentName"))
                        .address("")
                        .method("PASSWORD")
                        .ipAddress(ip)
                .build());
    }
}
