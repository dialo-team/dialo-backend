package com.fit.se.service.impl;

import com.fit.se.model.Account;
import com.fit.se.model.SecurityUser;
import com.fit.se.repository.AccountRepository;
import com.fit.se.service.SecurityUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityUserServiceImpl implements SecurityUserService {
    private final AccountRepository accountRepo;

    @Override
    public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException {
        Account account = accountRepo.findByPhone(phone)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with phone: " + phone));
        return new SecurityUser(account);
    }
}
