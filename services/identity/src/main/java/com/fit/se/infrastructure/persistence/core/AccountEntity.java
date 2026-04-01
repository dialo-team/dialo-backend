package com.fit.se.infrastructure.persistence.core;

import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.*;

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

    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true, mappedBy = "user")
    @Fetch(FetchMode.SELECT)
    @BatchSize(size = 20)
    private Collection<CredentialEntity> credentials = new LinkedList<>();


}
