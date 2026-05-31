package com.fit.se.friendship.persistence;

import com.fit.se.friendship.domain.aggregate.Friendship;
import com.fit.se.friendship.domain.valueobject.FriendshipSource;
import com.fit.se.friendship.domain.valueobject.FriendshipStatus;
import com.fit.se.user.persistence.node.UserNode;
import com.fit.se.friendship.persistence.relationship.FriendshipRelationship;

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
        relationship.setSource(friendship.getSource().name());
        relationship.setReason(friendship.getReason());
        return relationship;
    }

    public static Friendship toDomain(FriendshipRelationship relationship) {
        Friendship friendship = new Friendship(
                relationship.getFriendshipId(),
                relationship.getSenderId(),
                relationship.getReceiverId(),
                relationship.getRequestedAt(),
                relationship.getSource() == null ? FriendshipSource.DIRECT : FriendshipSource.valueOf(relationship.getSource()),
                relationship.getReason()
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