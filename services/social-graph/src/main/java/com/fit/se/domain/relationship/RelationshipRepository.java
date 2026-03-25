package com.fit.se.domain.relationship;

public interface RelationshipRepository {
    void createFriendRequest(String from, String to);

    void acceptFriend(String from, String to);

    void block(String from, String to);

    void removeFriendship(String from, String to);

    void removeRequest(String from, String to);

    boolean isFriend(String userA, String userB);

    boolean isBlocked(String userA, String userB);

    boolean hasPendingRequest(String from, String to);

    void unblock(String from, String to);

    void unfriend(String from, String to);

    void cancelFriendRequest(String from, String to);
}
