package com.fit.se.common.security;

import com.fit.se.auth.domain.account.Account;
import com.fit.se.auth.domain.credential.Credential;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class SecurityUser implements UserDetails {

    private final Account user;
    private final Credential credential;

    public SecurityUser(Account user, Credential credential) {
        this.user = user;
        this.credential = credential;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
//                .getRoles().stream()
//                .map(r -> new SimpleGrantedAuthority(r.getName()))
//                .toList();
    }

    public Account getUser() {
        return user;
    }

    @Override
    public String getPassword() {
        return credential.getSecretData();
    }

    @Override
    public String getUsername() {
        return user.getId();
    }

    @Override
    public boolean isAccountNonLocked() {
        return !user.isLocked();
    }

    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}
