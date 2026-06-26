package com.fit.se.auth.domain.account;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Account {
    @Setter
    private String id;

    private String phone;
    private String email;

    private boolean locked;
    private boolean enabled;
    private boolean profileProvisioned;

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

    public void markProfileProvisioned() {
        this.profileProvisioned = true;
    }

    public void markProfilePending() {
        this.profileProvisioned = false;
    }

    public boolean canSignIn() {
        return enabled && !locked && profileProvisioned;
    }

    public void changeEmail(String newEmail) {
        if (newEmail == null || newEmail.isBlank()) {
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
        account.markProfilePending();
        return account;
    }

    public static Account reconstitute(String id, String phone, String email, boolean locked, boolean enabled, boolean profileProvisioned) {
        Account account = new Account();
        account.id = id;
        account.phone = phone;
        account.email = email;
        account.locked = locked;
        account.enabled = enabled;
        account.profileProvisioned = profileProvisioned;
        return account;
    }
}
