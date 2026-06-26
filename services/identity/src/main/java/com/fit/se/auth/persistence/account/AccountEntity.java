package com.fit.se.auth.persistence.account;

import com.fit.se.auth.persistence.credential.CredentialEntity;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.Collection;
import java.util.LinkedList;

@Table
@Entity(name = "dialo_iam_user")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class AccountEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Access(AccessType.PROPERTY)
    private String id;

    @NotNull
    @Column(unique = true)
    private String phone;

    private String email;

    @Column
    @CreationTimestamp
    private Instant createdTimestamp;

    @Column
    @UpdateTimestamp
    private Instant lastmodifiedTimestamp;

    @Column
    private boolean locked;

    @Column
    private boolean enabled;

    @Column
    private Boolean profileProvisioned;

    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true, mappedBy = "user", fetch = FetchType.LAZY)
    @Fetch(FetchMode.SELECT)
    @BatchSize(size = 20)
    @Default
    private Collection<CredentialEntity> credentials = new LinkedList<>();
}
