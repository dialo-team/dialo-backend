package com.fit.se.domain.friendship;

import com.fit.se.domain.friendship.aggregate.Friendship;

import java.util.List;
import java.util.Optional;

public interface FriendshipRepository {
    Optional<Friendship> findById(String id);
    Optional<Friendship> findBetween(String userA, String userB);
    void save(Friendship friendship);

    boolean existsActiveOrPendingBetween(String s, String s1);

    void deleteBetween(String s, String s1);

    List<Friendship> findAcceptedOf(String current);
}