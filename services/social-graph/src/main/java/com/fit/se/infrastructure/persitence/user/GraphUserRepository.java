package com.fit.se.infrastructure.persitence.user;

import com.fit.se.domain.relationship.RelationshipRepository;
import com.fit.se.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class GraphUserRepository implements UserRepository, RelationshipRepository {
    private final Neo4jUserRepository neo4jUserRepo;
    private final Neo4jClient neo4jClient;

    @Override
    public void create() {
        UserEntity newUser = UserEntity.builder()
                .id(UUID.randomUUID())
                .username("Nam")
                .build();
        neo4jUserRepo.save(newUser);
    }


    @Override
    public void createFriendRequest(String from, String to) {
        neo4jClient.query("""
            MATCH (a:User {id: $from}), (b:User {id: $to})
            MERGE (a)-[:FRIEND_REQUEST]->(b)
        """).bindAll(Map.of("from", from, "to", to)).run();
    }

    @Override
    public void acceptFriend(String from, String to) {
        neo4jClient.query("""
            MATCH (a:User {id: $from})-[r:FRIEND_REQUEST]->(b:User {id: $to})
            DELETE r
            MERGE (a)-[:FRIEND]->(b)
            MERGE (b)-[:FRIEND]->(a)
        """).bindAll(Map.of("from", from, "to", to)).run();
    }

    @Override
    public void block(String from, String to) {
        neo4jClient.query("""
            MATCH (a:User {id: $from}), (b:User {id: $to})
            MERGE (a)-[:BLOCKED]->(b)
        """).bindAll(Map.of("from", from, "to", to)).run();
    }

    @Override
    public void removeFriendship(String from, String to) {
        neo4jClient.query("""
            MATCH (a:User {id: $from})-[r:FRIEND]-(b:User {id: $to})
            DELETE r
        """).bindAll(Map.of("from", from, "to", to)).run();
    }

    @Override
    public void removeRequest(String a, String b) {
        neo4jClient.query("""
            MATCH (a:User {id: $a}), (b:User {id: $b})
            OPTIONAL MATCH (a)-[r1:FRIEND_REQUEST]->(b)
            DELETE r1
            WITH a, b
            OPTIONAL MATCH (b)-[r2:FRIEND_REQUEST]->(a)
            DELETE r2
        """).bindAll(Map.of("a", a, "b", b)).run();
    }

    @Override
    public boolean isFriend(String a, String b) {
        return neo4jClient.query("""
            MATCH (a:User {id: $a})-[:FRIEND]->(b:User {id: $b})
            RETURN COUNT(*) > 0 AS result
        """).bindAll(Map.of("a", a, "b", b))
                .fetchAs(Boolean.class).one().orElse(false);
    }

    @Override
    public boolean isBlocked(String a, String b) {
        return neo4jClient.query("""
            MATCH (a:User {id: $a})
            MATCH (b:User {id: $b})
            RETURN EXISTS {
                MATCH (a)-[:BLOCKED]->(b)
            } OR EXISTS {
                MATCH (b)-[:BLOCKED]->(a)
            } AS result
        """).bindAll(Map.of("a", a, "b", b))
                .fetchAs(Boolean.class).one().orElse(false);
    }

    @Override
    public boolean hasPendingRequest(String from, String to) {
        return neo4jClient.query("""
            MATCH (a:User {id: $from})-[:FRIEND_REQUEST]->(b:User {id: $to})
            RETURN COUNT(*) > 0 AS result
        """).bindAll(Map.of("from", from, "to", to))
                .fetchAs(Boolean.class).one().orElse(false);
    }

    @Override
    public void unblock(String from, String to) {
        neo4jClient.query("""
            MATCH (a:User {id: $from})
            MATCH (b:User {id: $to})
            OPTIONAL MATCH (a)-[r:BLOCKED]->(b)
            DELETE r
        """).bindAll(Map.of("from", from, "to", to)).run();
    }

    @Override
    public void unfriend(String from, String to) {
        neo4jClient.query("""
            MATCH (a:User {id: $from})
            MATCH (b:User {id: $to})
            OPTIONAL MATCH (a)-[r:FRIEND]-(b)
            DELETE r
        """).bindAll(Map.of("from", from, "to", to)).run();
    }

    @Override
    public void cancelFriendRequest(String from, String to) {
        neo4jClient.query("""
            MATCH (a:User {id: $from})
            MATCH (b:User {id: $to})
    
            OPTIONAL MATCH (a)-[r1:FRIEND_REQUEST]->(b)
            DELETE r1
    
            WITH a, b
            OPTIONAL MATCH (b)-[r2:FRIEND_REQUEST]->(a)
            DELETE r2
        """).bindAll(Map.of("from", from, "to", to)).run();
    }
}
