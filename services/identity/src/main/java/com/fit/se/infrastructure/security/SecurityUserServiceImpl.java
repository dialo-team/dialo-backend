package com.fit.se.infrastructure.security;

import com.fit.se.domain.user.Account;
import com.fit.se.domain.user.AccountRepository;
import com.fit.se.domain.user.Credential;
import com.fit.se.domain.user.CredentialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityUserServiceImpl implements SecurityUserService {
    private final AccountRepository userRepo;
    private final CredentialRepository credentialRepo;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        Account user = userRepo.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with userId: " + userId));
        System.out.println("user: " + user.getId());

        Credential credential = credentialRepo.findByUser(user.getId());
        return new SecurityUser(user, credential);
    }
}
