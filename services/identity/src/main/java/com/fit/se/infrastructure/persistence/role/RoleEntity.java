package com.fit.se.infrastructure.persistence.role;

import jakarta.persistence.*;
import lombok.*;

@Table
@Entity(name = "dialo_iam_role")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Access(AccessType.PROPERTY)
    private String id;

    private String name;

    private String description;
}
