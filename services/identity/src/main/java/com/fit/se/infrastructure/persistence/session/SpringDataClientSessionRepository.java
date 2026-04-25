package com.fit.se.infrastructure.persistence.session;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataClientSessionRepository extends JpaRepository<ClientSessionEntity, String> {

    List<ClientSessionEntity> findByUserIdAndStatus(String userId, String status);

    List<ClientSessionEntity> findByUserIdAndStatusNot(String userId, String status);
}
