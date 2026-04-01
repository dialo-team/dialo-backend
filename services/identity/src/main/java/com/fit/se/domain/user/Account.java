package com.fit.se.domain.user;

import lombok.*;

@Getter
public class Account {
    @Setter
    private String id;

    private String phone;
    private String email;

    private boolean locked;
    private boolean enabled;

    public void lock() {
        this.locked = true;
    }

    public void unlock() {
        this.locked = false;
    }

    public void enabled() {
        this.enabled = true;
    }

    public void disable() {
        this.enabled = false;
    }

    public boolean canSignIn() {
        return enabled && !locked;
    }

    public void changeEmail(String newEmail) {
        if(newEmail == null || newEmail.isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
        this.email = newEmail;
    }

    public void changePhone(String newPhone) {
        this.phone = newPhone;
    }

    public boolean hasEmail() {
        return this.email != null;
    }

    public static Account create(String phone) {
        Account account = new Account();
        account.changePhone(phone);
        account.enabled();
        account.unlock();
        return account;
    }
}
