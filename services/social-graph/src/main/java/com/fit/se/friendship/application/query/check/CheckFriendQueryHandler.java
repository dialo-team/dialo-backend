package com.fit.se.friendship.application.query.check;

import com.fit.se.blocks.domain.BlockRelationRepository;
import com.fit.se.friendship.domain.FriendshipRepository;
import com.fit.se.friendship.domain.aggregate.Friendship;
import com.fit.se.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CheckFriendQueryHandler {
    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;
    private final BlockRelationRepository blockRelationRepository;

    public CheckFriendResult execute(CheckFriendQuery query) {
        validate(query);

        userRepository.findById(query.currentUserId())
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay nguoi dung hien tai"));
        userRepository.findById(query.targetUserId())
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay nguoi dung dich"));

        if (query.currentUserId().equals(query.targetUserId())) {
            return CheckFriendResult.builder()
                    .currentUserId(query.currentUserId())
                    .targetUserId(query.targetUserId())
                    .relationStatus("SELF")
                    .isSelf(true)
                    .build();
        }

        boolean blocked = blockRelationRepository.existsActiveBlockBetween(query.currentUserId(), query.targetUserId());
        if (blocked) {
            return CheckFriendResult.builder()
                    .currentUserId(query.currentUserId())
                    .targetUserId(query.targetUserId())
                    .relationStatus("BLOCKED")
                    .canMessage(true)
                    .blocked(true)
                    .build();
        }

        Friendship friendship = friendshipRepository.findBetween(query.currentUserId(), query.targetUserId())
                .orElse(null);

        if (friendship == null) {
            return CheckFriendResult.builder()
                    .currentUserId(query.currentUserId())
                    .targetUserId(query.targetUserId())
                    .relationStatus("NONE")
                    .canAddFriend(true)
                    .canMessage(true)
                    .build();
        }

        if (friendship.isAccepted()) {
            return CheckFriendResult.builder()
                    .currentUserId(query.currentUserId())
                    .targetUserId(query.targetUserId())
                    .relationStatus("FRIEND")
                    .canMessage(true)
                    .canCall(true)
                    .canUnfriend(true)
                    .build();
        }

        if (friendship.isPending()) {
            if (friendship.getSenderId().equals(query.currentUserId())) {
                return CheckFriendResult.builder()
                        .currentUserId(query.currentUserId())
                        .targetUserId(query.targetUserId())
                        .relationStatus("OUTGOING_REQUEST")
                        .canCancelRequest(true)
                        .canMessage(true)
                        .build();
            }

            return CheckFriendResult.builder()
                    .currentUserId(query.currentUserId())
                    .targetUserId(query.targetUserId())
                    .relationStatus("INCOMING_REQUEST")
                    .canAcceptRequest(true)
                    .canRejectRequest(true)
                    .canMessage(true)
                    .build();
        }

        return CheckFriendResult.builder()
                .currentUserId(query.currentUserId())
                .targetUserId(query.targetUserId())
                .relationStatus("NONE")
                .canAddFriend(true)
                .canMessage(true)
                .build();
    }

    private void validate(CheckFriendQuery query) {
        if (query == null) {
            throw new IllegalArgumentException("Thieu du lieu kiem tra trang thai ket ban");
        }
        if (query.currentUserId() == null || query.currentUserId().isBlank()) {
            throw new IllegalArgumentException("Thieu ma nguoi dung hien tai");
        }
        if (query.targetUserId() == null || query.targetUserId().isBlank()) {
            throw new IllegalArgumentException("Thieu ma nguoi dung dich");
        }
    }
}