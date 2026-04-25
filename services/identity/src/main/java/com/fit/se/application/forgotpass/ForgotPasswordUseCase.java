package com.fit.se.application.forgotpass;

import com.fit.se.application.token.generate.GenerateTokenResult;
import com.fit.se.domain.otp.OtpType;
import com.fit.se.domain.session.ClientSession;
import com.fit.se.domain.session.ClientSessionRepository;
import com.fit.se.domain.user.Account;
import com.fit.se.domain.user.AccountRepository;
import com.fit.se.domain.user.Credential;
import com.fit.se.domain.user.CredentialRepository;
import com.fit.se.infrastructure.external.otp.OtpService;
import com.fit.se.infrastructure.security.hasher.HasherFactory;
import com.fit.se.infrastructure.security.token.TokenProvider;
import com.fit.se.infrastructure.security.token.TokenPurpose;
import com.fit.se.infrastructure.security.token.TokenStore;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ForgotPasswordUseCase {
    private final OtpService otpService;
    private final AccountRepository accountRepo;
    private final CredentialRepository credentialRepo;
    private final TokenProvider tokenProvider;
    private final TokenStore tokenStore;
    private final HasherFactory hasher;
    private final ClientSessionRepository clientSessionRepo;
    private final UserAgentAnalyzer analyzer;

    public void request(String source, String type) {
        switch (type) {
            case "SMS" -> {
                if(!accountRepo.existsByPhone(source)) {
                    throw new RuntimeException("Số điện thoại không tồn tại");
                }
                otpService.send(source, OtpType.SMS);
            }
            case "EMAIL" -> {
                if(!accountRepo.existsByEmail(source)) {
                    throw new RuntimeException("Email của bạn không chính xác");
                }
                otpService.send(source, OtpType.EMAIL);
            }
        }
    }

    public String confirm(String source, String type, String otp) {
        if(otpService.validate(source, otp)) {
            Account storedAccount = type.equals("SMS")
                    ? accountRepo.findByPhone(source).orElseThrow()
                    : accountRepo.findByEmail(source).orElseThrow();

            String resetToken = tokenProvider.generate(TokenPurpose.RESET, storedAccount.getId());

            tokenStore.storeResetToken(resetToken, storedAccount.getId(), tokenProvider.extractJti(resetToken));

            return resetToken;
        }
        return null;
    }

    @Transactional
    public GenerateTokenResult execute(String password, String authorizationHeader, HttpServletRequest http) {
        String accountId = tokenProvider.extractSubject(authorizationHeader);

        Credential credential = credentialRepo.findByUser(accountId);
        String salt = hasher.generateSalt();
        String hashPassword = hasher.hash(password, salt);

        credential.rotateSecret(hashPassword, salt.getBytes());
        credentialRepo.save(credential);

        String accessToken = tokenProvider.generate(TokenPurpose.ACCESS, accountId);
        String refreshToken = tokenProvider.generate(TokenPurpose.REFRESH, accountId);

        String ua = http.getHeader("User-Agent");
        String ip = http.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) {
            ip = http.getRemoteAddr();
        }
        UserAgent agent = analyzer.parse(ua == null ? "" : ua);
        tokenStore.store(refreshToken, accountId, tokenProvider.extractJti(refreshToken), agent.getValue("AgentName"));
        clientSessionRepo.save(ClientSession.builder()
                .userId(accountId)
                .deviceName(agent.getValue("DeviceClass"))
                .agentName(agent.getValue("AgentName"))
                .address("")
                .method("PASSWORD")
                .ipAddress(ip)
                .build());

        return GenerateTokenResult.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .build();
    }
}
