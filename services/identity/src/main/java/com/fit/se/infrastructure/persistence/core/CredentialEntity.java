package com.fit.se.infrastructure.persistence.core;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity(name = "dialo_iam_credential")
@Table
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CredentialEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Access(AccessType.PROPERTY)
    private String id;

    private String type;

    private String secretData;

    private byte[] salt;

    @CreationTimestamp
    private Instant createdDate;
    @UpdateTimestamp
    private Instant lastModified;

    private String credentialData;

    private int priority;

    @Version
    private int version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private AccountEntity user;
}
