package com.fit.se.domain.user;

import lombok.*;

import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Account {
    private String id;

    private String phone;
    private String password;
    private String email;
    private boolean locked;
    private boolean enabled;

    public void lock() {
        this.locked = true;
    }

    public void unLock() {
        this.locked = false;
    }

    public boolean canSignIn() {
        return enabled && !locked;
    }

    public void changePassword(String newPass) {
        // business logic
        this.password = newPass;
    }

    public void changeEmail(String newEmail) {
        // business logic
        this.email = newEmail;
    }
}
