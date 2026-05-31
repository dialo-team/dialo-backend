package com.fit.se.friendship.application.command.cancel;

import com.fit.se.friendship.domain.FriendshipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CancelFriendCommandHandler {

    private final FriendshipRepository friendshipRepository;

    public void execute(CancelFriendCommand cmd) {
        var friendship = friendshipRepository.findBetween(cmd.senderId(), cmd.receiverId())
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay loi moi ket ban"));

        if (!friendship.isPending()) {
            throw new IllegalStateException("Chi co the huy loi moi ket ban dang cho phan hoi");
        }

        if (!friendship.getSenderId().equals(cmd.senderId())) {
            throw new IllegalStateException("Chi nguoi da gui loi moi moi co the huy loi moi nay");
        }

        if (!friendship.getReceiverId().equals(cmd.receiverId())) {
            throw new IllegalStateException("Nguoi nhan loi moi khong hop le");
        }

        friendshipRepository.deleteBetween(friendship.getSenderId(), friendship.getReceiverId());
    }
}