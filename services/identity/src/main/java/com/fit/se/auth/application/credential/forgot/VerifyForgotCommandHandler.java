package com.fit.se.auth.application.credential.forgot;

import com.fit.se.auth.domain.account.Account;
import com.fit.se.auth.domain.account.AccountRepository;
import com.fit.se.common.security.token.TokenProvider;
import com.fit.se.common.security.token.TokenPurpose;
import com.fit.se.common.security.token.TokenStore;
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

