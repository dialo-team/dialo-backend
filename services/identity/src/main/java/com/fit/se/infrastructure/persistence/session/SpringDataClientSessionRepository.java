package com.fit.se.infrastructure.persistence.session;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataClientSessionRepository extends JpaRepository<ClientSessionEntity, ClientSessionEntity.Key> {
}
