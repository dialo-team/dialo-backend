package com.fit.se.identity.user;


import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    void updateProfileInfo();

    void changePassword();


}
