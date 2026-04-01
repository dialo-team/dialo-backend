package com.fit.se.infrastructure.persistence.user;

import com.fit.se.domain.user.aggregate.User;
import com.fit.se.infrastructure.persistence.node.UserNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.Optional;

public interface SpringDataUserNeo4jRepository extends Neo4jRepository<UserNode, String> {

    @Query("""
        MATCH (u:User {id: $userId})
        RETURN u
        """)
    Optional<UserNode> findUserById(String userId);

    @Query("""
        MATCH (a:User {id: $userA})-[r:FRIENDSHIP]->(b:User {id: $userB})
        WHERE r.status = 'ACCEPTED' OR r.status = 'PENDING' OR r.status = 'REJECTED' OR r.status = 'CANCELLED' OR r.status = 'UNFRIENDED'
        RETURN a, collect(r), collect(b)
        UNION
        MATCH (b:User {id: $userB})-[r:FRIENDSHIP]->(a:User {id: $userA})
        WHERE r.status = 'ACCEPTED' OR r.status = 'PENDING' OR r.status = 'REJECTED' OR r.status = 'CANCELLED' OR r.status = 'UNFRIENDED'
        RETURN a, collect(r), collect(b)
        """)
    Optional<UserNode> findUserWithFriendshipBetween(String userA, String userB);

    @Query("""
        MATCH (a:User {id: $userA})-[r:BLOCK]->(b:User {id: $userB})
        WHERE r.status = 'ACTIVE'
        RETURN a, collect(r), collect(b)
        UNION
        MATCH (b:User {id: $userB})-[r:BLOCK]->(a:User {id: $userA})
        WHERE r.status = 'ACTIVE'
        RETURN a, collect(r), collect(b)
        """)
    Optional<UserNode> findUserWithActiveBlockBetween(String userA, String userB);

    @Query("""
    MATCH (u:User {qrTokenValue: $qrToken})
    RETURN u
    """)
    Optional<UserNode> findByQrToken(String qrToken);

    @Query("""
        MATCH (u:User {phone: $phone})
        RETURN u
    """)
    Optional<UserNode> findByPhone(String phone);
}