package com.fit.se.friendship.application.command.reject;

import com.fit.se.friendship.domain.FriendshipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RejectFriendCommandHandler {

    private final FriendshipRepository friendshipRepository;

    public void execute(RejectFriendCommand cmd) {
        var friendship = friendshipRepository.findBetween(cmd.senderId(), cmd.receiverId())
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay loi moi ket ban"));

        if (!friendship.isPending()) {
            throw new IllegalStateException("Chi co the tu choi loi moi ket ban dang cho phan hoi");
        }

        if (!friendship.getSenderId().equals(cmd.receiverId())) {
            throw new IllegalStateException("Nguoi gui loi moi khong hop le");
        }

        if (!friendship.getReceiverId().equals(cmd.senderId())) {
            throw new IllegalStateException("Chi nguoi nhan moi co the tu choi loi moi nay");
        }

        friendshipRepository.deleteBetween(friendship.getSenderId(), friendship.getReceiverId());
    }
}