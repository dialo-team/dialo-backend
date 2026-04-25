package com.fit.se.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity(name = "dialo_person")
@Table(name = "dialo_person")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PersonEntity {

    @Id
    @Column(name = "person_id", nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String avatar;
}