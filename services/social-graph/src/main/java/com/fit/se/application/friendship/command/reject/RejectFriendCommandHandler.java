package com.fit.se.application.friendship.command.reject;

import com.fit.se.domain.friendship.FriendshipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RejectFriendCommandHandler {

    private final FriendshipRepository friendshipRepository;

    public void execute(RejectFriendCommand cmd) {
        var friendship = friendshipRepository.findBetween(cmd.senderId(), cmd.receiverId())
                .orElseThrow(() -> new IllegalArgumentException("Friend request not found"));

        if (!friendship.isPending()) {
            throw new IllegalStateException("Only pending friend request can be rejected");
        }

        if (!friendship.getSenderId().equals(cmd.receiverId())) {
            throw new IllegalStateException("Invalid requester");
        }

        if (!friendship.getReceiverId().equals(cmd.senderId())) {
            throw new IllegalStateException("Only receiver can reject this friend request");
        }

        friendshipRepository.deleteBetween(friendship.getSenderId(), friendship.getReceiverId());
    }
}