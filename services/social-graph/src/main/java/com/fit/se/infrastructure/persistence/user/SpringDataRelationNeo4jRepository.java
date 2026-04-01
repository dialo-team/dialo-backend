package com.fit.se.infrastructure.persistence.user;

import com.fit.se.infrastructure.persistence.node.UserNode;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface SpringDataRelationNeo4jRepository extends Repository<UserNode, String> {

    @Query("""
        MATCH (a:User {id: $userA})-[r:FRIENDSHIP]->(b:User {id: $userB})
        RETURN r.friendshipId
        UNION
        MATCH (b:User {id: $userB})-[r:FRIENDSHIP]->(a:User {id: $userA})
        RETURN r.friendshipId
        LIMIT 1
        """)
    Optional<String> findFriendshipIdBetween(String userA, String userB);

    @Query("""
        MATCH (a:User {id: $userA})-[r:BLOCK]->(b:User {id: $userB})
        WHERE r.status = 'ACTIVE'
        RETURN r.blockId
        UNION
        MATCH (b:User {id: $userB})-[r:BLOCK]->(a:User {id: $userA})
        WHERE r.status = 'ACTIVE'
        RETURN r.blockId
        LIMIT 1
        """)
    Optional<String> findActiveBlockIdBetween(String userA, String userB);
}