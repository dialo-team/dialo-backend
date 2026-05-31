package com.fit.se.friendship.persistence.adapter;

import com.fit.se.friendship.domain.FriendshipRepository;
import com.fit.se.friendship.domain.aggregate.Friendship;
import com.fit.se.friendship.persistence.relationship.FriendshipRelationship;
import com.fit.se.friendship.persistence.FriendshipPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class Neo4jFriendshipRepositoryAdapter implements FriendshipRepository {

    private final Neo4jClient neo4jClient;

    @Override
    public Optional<Friendship> findById(String id) {
        String cypher = """
            MATCH (:User)-[r:FRIENDSHIP {friendshipId: $id}]->(:User)
            RETURN r
            LIMIT 1
            """;

        return neo4jClient.query(cypher)
                .bind(id).to("id")
                .fetchAs(FriendshipRelationship.class)
                .mappedBy((typeSystem, record) -> mapRelationship(record))
                .one()
                .map(FriendshipPersistenceMapper::toDomain);
    }

    @Override
    public Optional<Friendship> findBetween(String userA, String userB) {
        String cypher = """
            MATCH (a:User {id: $userA})-[r:FRIENDSHIP]->(b:User {id: $userB})
            RETURN r
            UNION
            MATCH (b:User {id: $userB})-[r:FRIENDSHIP]->(a:User {id: $userA})
            RETURN r
            LIMIT 1
            """;

        return neo4jClient.query(cypher)
                .bind(userA).to("userA")
                .bind(userB).to("userB")
                .fetchAs(FriendshipRelationship.class)
                .mappedBy((typeSystem, record) -> mapRelationship(record))
                .one()
                .map(FriendshipPersistenceMapper::toDomain);
    }

    @Override
    public void save(Friendship friendship) {
        String cypher = """
        MATCH (sender:User {id: $senderId})
        MATCH (receiver:User {id: $receiverId})
        MERGE (sender)-[r:FRIENDSHIP {friendshipId: $friendshipId}]->(receiver)
        SET r.senderId = $senderId,
            r.receiverId = $receiverId,
            r.status = $status,
            r.requestedAt = $requestedAt,
            r.respondedAt = $respondedAt,
            r.acceptedAt = $acceptedAt,
            r.unfriendedAt = $unfriendedAt,
            r.source = $source,
            r.reason = $reason
        """;

        neo4jClient.query(cypher)
                .bind(friendship.getId()).to("friendshipId")
                .bind(friendship.getSenderId()).to("senderId")
                .bind(friendship.getReceiverId()).to("receiverId")
                .bind(friendship.getStatus().name()).to("status")
                .bind(toOffsetDateTime(friendship.getRequestedAt())).to("requestedAt")
                .bind(toOffsetDateTime(friendship.getRespondedAt())).to("respondedAt")
                .bind(toOffsetDateTime(friendship.getAcceptedAt())).to("acceptedAt")
                .bind(toOffsetDateTime(friendship.getUnfriendedAt())).to("unfriendedAt")
                .bind(friendship.getSource().name()).to("source")
                .bind(friendship.getReason()).to("reason")
                .run();
    }

    @Override
    public boolean existsActiveOrPendingBetween(String userAId, String userBId) {
        String cypher = """
        MATCH (a:User {id: $userAId})-[r:FRIENDSHIP]-(b:User {id: $userBId})
        WHERE r.status IN ['PENDING', 'ACCEPTED']
        RETURN COUNT(r) > 0 AS exists
        """;

        return neo4jClient.query(cypher)
                .bind(userAId).to("userAId")
                .bind(userBId).to("userBId")
                .fetchAs(Boolean.class)
                .mappedBy((typeSystem, record) -> record.get("exists").asBoolean())
                .one()
                .orElse(false);
    }

    @Override
    public void deleteBetween(String userAId, String userBId) {
        String cypher = """
            MATCH (a:User {id: $userAId})-[r:FRIENDSHIP]-(b:User {id: $userBId})
            WHERE r.status IN ['PENDING', 'ACCEPTED']
            DELETE r
        """;

        neo4jClient.query(cypher)
                .bind(userAId).to("userAId")
                .bind(userBId).to("userBId")
                .run();
    }

    @Override
    public List<Friendship> findAcceptedOf(String current) {
        String cypher = """
        MATCH (:User {id: $current})-[r:FRIENDSHIP]->(:User)
        WHERE r.status = 'ACCEPTED'
        RETURN r
        UNION
        MATCH (:User)-[r:FRIENDSHIP]->(:User {id: $current})
        WHERE r.status = 'ACCEPTED'
        RETURN r
        """;

        return neo4jClient.query(cypher)
                .bind(current).to("current")
                .fetchAs(FriendshipRelationship.class)
                .mappedBy((typeSystem, record) -> mapRelationship(record))
                .all()
                .stream()
                .map(FriendshipPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public List<Friendship> findPendingReceivedOf(String current) {
        String cypher = """
        MATCH (:User)-[r:FRIENDSHIP]->(:User {id: $current})
        WHERE r.status = 'PENDING'
        RETURN r
        ORDER BY r.requestedAt DESC
        """;

        return neo4jClient.query(cypher)
                .bind(current).to("current")
                .fetchAs(FriendshipRelationship.class)
                .mappedBy((typeSystem, record) -> mapRelationship(record))
                .all()
                .stream()
                .map(FriendshipPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public List<Friendship> findPendingSentOf(String current) {
        String cypher = """
        MATCH (:User {id: $current})-[r:FRIENDSHIP]->(:User)
        WHERE r.status = 'PENDING'
        RETURN r
        ORDER BY r.requestedAt DESC
        """;

        return neo4jClient.query(cypher)
                .bind(current).to("current")
                .fetchAs(FriendshipRelationship.class)
                .mappedBy((typeSystem, record) -> mapRelationship(record))
                .all()
                .stream()
                .map(FriendshipPersistenceMapper::toDomain)
                .toList();
    }

    private FriendshipRelationship mapRelationship(org.neo4j.driver.Record record) {
        FriendshipRelationship r = new FriendshipRelationship();
        r.setFriendshipId(record.get("r").get("friendshipId").asString());
        r.setSenderId(record.get("r").get("senderId").asString());
        r.setReceiverId(record.get("r").get("receiverId").asString());
        r.setStatus(record.get("r").get("status").asString());

        if (!record.get("r").get("requestedAt").isNull()) {
            r.setRequestedAt(record.get("r").get("requestedAt").asZonedDateTime().toInstant());
        }
        if (!record.get("r").get("respondedAt").isNull()) {
            r.setRespondedAt(record.get("r").get("respondedAt").asZonedDateTime().toInstant());
        }
        if (!record.get("r").get("acceptedAt").isNull()) {
            r.setAcceptedAt(record.get("r").get("acceptedAt").asZonedDateTime().toInstant());
        }
        if (!record.get("r").get("unfriendedAt").isNull()) {
            r.setUnfriendedAt(record.get("r").get("unfriendedAt").asZonedDateTime().toInstant());
        }
        if (!record.get("r").get("source").isNull()) {
            r.setSource(record.get("r").get("source").asString());
        }
        if (!record.get("r").get("reason").isNull()) {
            r.setReason(record.get("r").get("reason").asString());
        }
        return r;
    }

    private OffsetDateTime toOffsetDateTime(Instant value) {
        return value == null ? null : value.atOffset(ZoneOffset.UTC);
    }
}