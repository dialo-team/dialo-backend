package com.fit.se.application.friendship.command.cancel;

import com.fit.se.domain.friendship.FriendshipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CancelFriendCommandHandler {

    private final FriendshipRepository friendshipRepository;

    public void execute(CancelFriendCommand cmd) {
        var friendship = friendshipRepository.findBetween(cmd.senderId(), cmd.receiverId())
                .orElseThrow(() -> new IllegalArgumentException("Friend request not found"));

        if (!friendship.isPending()) {
            throw new IllegalStateException("Only pending friend request can be cancelled");
        }

        if (!friendship.getSenderId().equals(cmd.senderId())) {
            throw new IllegalStateException("Only requester can cancel this friend request");
        }

        if (!friendship.getReceiverId().equals(cmd.receiverId())) {
            throw new IllegalStateException("Invalid receiver");
        }

        friendshipRepository.deleteBetween(friendship.getSenderId(), friendship.getReceiverId());
    }
}