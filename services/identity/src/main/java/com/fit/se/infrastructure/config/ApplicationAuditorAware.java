package com.fit.se.infrastructure.config;

import com.fit.se.domain.user.Account;
import com.fit.se.infrastructure.security.SecurityUser;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;
import java.util.Optional;

public class ApplicationAuditorAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        final Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return Optional.of("SYSTEM");
        }

        final SecurityUser user = (SecurityUser) authentication.getPrincipal();
        Account account = user.getUser();
        return Optional.ofNullable(Objects.requireNonNull(account).getId());
    }
}
