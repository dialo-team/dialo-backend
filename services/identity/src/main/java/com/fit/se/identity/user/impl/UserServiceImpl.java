package com.fit.se.identity.user.impl;

import com.fit.se.identity.user.UserMapper;
import com.fit.se.identity.user.UserRepository;
import com.fit.se.identity.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public void updateProfileInfo() {

    }

    @Override
    public void changePassword() {

    }

    @Override
    public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException {
        System.out.println("Hello");
        UserDetails userDetails = this.userRepo.findByPhone(phone)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with phone: " + phone));
        System.out.println(userDetails.getUsername());
        return userDetails;
    }
}
