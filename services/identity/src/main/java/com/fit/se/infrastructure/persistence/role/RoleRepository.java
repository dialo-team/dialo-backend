package com.fit.se.infrastructure.persistence.role;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<RoleEntity, String> {
    boolean existsByName(String name);
}
