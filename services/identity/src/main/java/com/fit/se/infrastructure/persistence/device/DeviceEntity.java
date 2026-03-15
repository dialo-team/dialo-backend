package com.fit.se.infrastructure.persistence.device;

import com.fit.se.infrastructure.persistence.user.AccountEntity;
import jakarta.persistence.*;

import java.time.Instant;

@Table
@Entity(name = "devices")
public class DeviceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String ipAddress;
    private String loginMethod;

    private Instant lastLoginAt;
    private Instant lastActiveAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private AccountEntity account;
}
