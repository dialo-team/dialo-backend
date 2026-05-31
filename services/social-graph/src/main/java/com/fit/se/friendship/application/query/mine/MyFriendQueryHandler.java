package com.fit.se.friendship.application.query.mine;

import com.fit.se.friendship.domain.FriendshipRepository;
import com.fit.se.friendship.domain.aggregate.Friendship;
import com.fit.se.user.domain.UserRepository;
import com.fit.se.user.domain.aggregate.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MyFriendQueryHandler {

    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;

    public MyFriendResult execute(MyFriendQuery query) {
        if (query == null || query.current() == null || query.current().isBlank()) {
            throw new IllegalArgumentException("Current user id must not be blank");
        }

        List<Friendship> friendships = friendshipRepository.findAcceptedOf(query.current());

        List<MyFriendResult.Item> items = friendships.stream()
                .map(friendship -> toItem(friendship, query.current()))
                .toList();

        return MyFriendResult.builder()
                .friends(items)
                .build();
    }

    private MyFriendResult.Item toItem(Friendship friendship, String currentUserId) {
        String friendId = friendship.otherOf(currentUserId);

        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new IllegalArgumentException("Friend user not found: " + friendId));

        return MyFriendResult.Item.builder()
                .friendshipId(friendship.getId())
                .friendId(friend.getId())
                .friendUserName(friend.getUserName())
                .friendAvatar(friend.getAppearance() != null ? friend.getAppearance().getAvatar() : null)
                .since(friendship.getAcceptedAt())
                .build();
    }
}