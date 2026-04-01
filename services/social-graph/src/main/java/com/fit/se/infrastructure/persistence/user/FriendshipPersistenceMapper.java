package com.fit.se.infrastructure.persistence.user;

import com.fit.se.domain.friendship.aggregate.Friendship;
import com.fit.se.domain.friendship.valueobject.FriendshipStatus;
import com.fit.se.infrastructure.persistence.node.UserNode;
import com.fit.se.infrastructure.persistence.relationship.FriendshipRelationship;

public final class FriendshipPersistenceMapper {

    private FriendshipPersistenceMapper() {
    }

    public static FriendshipRelationship toRelationship(Friendship friendship, UserNode targetUser) {
        FriendshipRelationship relationship = new FriendshipRelationship(targetUser);
        relationship.setFriendshipId(friendship.getId());
        relationship.setSenderId(friendship.getSenderId());
        relationship.setReceiverId(friendship.getReceiverId());
        relationship.setStatus(friendship.getStatus().name());
        relationship.setRequestedAt(friendship.getRequestedAt());
        relationship.setRespondedAt(friendship.getRespondedAt());
        relationship.setAcceptedAt(friendship.getAcceptedAt());
        relationship.setUnfriendedAt(friendship.getUnfriendedAt());
        return relationship;
    }

    public static Friendship toDomain(FriendshipRelationship relationship) {
        Friendship friendship = new Friendship(
                relationship.getFriendshipId(),
                relationship.getSenderId(),
                relationship.getReceiverId(),
                relationship.getRequestedAt()
        );

        FriendshipStatus status = FriendshipStatus.valueOf(relationship.getStatus());
        if (status == FriendshipStatus.ACCEPTED) {
            friendship.accept(relationship.getAcceptedAt());
        } else if (status == FriendshipStatus.REJECTED) {
            friendship.reject(relationship.getRespondedAt());
        } else if (status == FriendshipStatus.CANCELLED) {
            friendship.cancelRequest(relationship.getRespondedAt());
        } else if (status == FriendshipStatus.UNFRIENDED) {
            friendship.accept(relationship.getAcceptedAt());
            friendship.unfriend(relationship.getUnfriendedAt());
        }

        return friendship;
    }
}