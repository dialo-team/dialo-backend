package com.fit.se.infrastructure.persitence.user;

import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.UUID;

public interface Neo4jUserRepository extends Neo4jRepository<UserEntity, UUID> {
}
