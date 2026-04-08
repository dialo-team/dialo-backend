package com.fit.se.application.credential.forgot;

import com.fit.se.domain.user.Account;
import com.fit.se.domain.user.AccountRepository;
import com.fit.se.infrastructure.security.token.TokenProvider;
import com.fit.se.infrastructure.security.token.TokenPurpose;
import com.fit.se.infrastructure.security.token.TokenStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VerifyForgotCommandHandler {
    private final TokenStore store;
    private final TokenProvider tokenProvider;
    private final AccountRepository userRepo;

    public VerifyForgotResult execute(VerifyForgotCommand cmd) {
        Account user = userRepo.findByPhone(cmd.phone()).orElseThrow();

        String resetToken = tokenProvider.generate(TokenPurpose.RESET, user.getId());

        store.storeResetToken(resetToken, user.getId(), tokenProvider.extractJti(resetToken));

        return VerifyForgotResult.builder()
                .resetToken(resetToken)
                .build();
    }
}
