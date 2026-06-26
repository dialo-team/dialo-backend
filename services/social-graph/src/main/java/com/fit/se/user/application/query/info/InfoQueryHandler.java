package com.fit.se.user.application.query.info;

import com.fit.se.blocks.domain.BlockRelationRepository;
import com.fit.se.friendship.domain.FriendshipRepository;
import com.fit.se.friendship.domain.aggregate.Friendship;
import com.fit.se.user.domain.UserRepository;
import com.fit.se.user.domain.aggregate.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InfoQueryHandler {

    private final UserRepository userRepository;
    private final BlockRelationRepository blockRepository;
    private final FriendshipRepository friendshipRepository;

    public InfoResult execute(InfoQuery query) {
        validate(query);

        User targetUser = switch (query.type()) {
            case ID -> handleLookupById(query);
            case PHONE -> handleLookupByPhone(query);
            case QR -> handleLookupByQr(query);
        };

        ensureNotBlocked(query.current(), targetUser.getId());

        return toResult(query.current(), targetUser);
    }

    private void validate(InfoQuery query) {
        if (query == null) {
            throw new IllegalArgumentException("Thieu du lieu truy van nguoi dung");
        }
        if (query.current() == null || query.current().isBlank()) {
            throw new IllegalArgumentException("Thieu ma nguoi dung hien tai");
        }
        if (query.target() == null || query.target().isBlank()) {
            throw new IllegalArgumentException("Thieu gia tri tim kiem");
        }
        if (query.type() == null) {
            throw new IllegalArgumentException("Thieu kieu tra cuu nguoi dung");
        }
    }

    private User handleLookupById(InfoQuery query) {
        return userRepository.findById(query.target())
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay nguoi dung"));
    }

    private User handleLookupByPhone(InfoQuery query) {
        return userRepository.findByPhone(query.target())
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay nguoi dung"));
    }

    private User handleLookupByQr(InfoQuery query) {
        return userRepository.findByQrToken(query.target())
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay nguoi dung"));
    }

    private void ensureNotBlocked(String currentUserId, String targetUserId) {
        if (blockRepository.existsActiveBlockBetween(currentUserId, targetUserId)) {
            throw new IllegalArgumentException("Khong tim thay nguoi dung");
        }
    }

    private InfoResult toResult(String currentUserId, User user) {
        RelationState state = resolveRelationState(currentUserId, user.getId());

        return InfoResult.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .bio(user.getProfile().getBio())
                .avatar(user.getAppearance().getAvatar())
                .background(user.getAppearance().getBackground())
                .theme(user.getAppearance().getTheme())
                .relationStatus(state.relationStatus())
                .isSelf(state.isSelf())
                .canAddFriend(state.canAddFriend())
                .canCancelRequest(state.canCancelRequest())
                .canAcceptRequest(state.canAcceptRequest())
                .canRejectRequest(state.canRejectRequest())
                .canMessage(state.canMessage())
                .canCall(state.canCall())
                .canUnfriend(state.canUnfriend())
                .build();
    }

    private RelationState resolveRelationState(String currentUserId, String targetUserId) {
        if (currentUserId.equals(targetUserId)) {
            return new RelationState("SELF", true, false, false, false, false, false, false, false);
        }

        Friendship friendship = friendshipRepository.findBetween(currentUserId, targetUserId).orElse(null);
        if (friendship == null) {
            return new RelationState("NONE", false, true, false, false, false, true, false, false);
        }

        if (friendship.isAccepted()) {
            return new RelationState("FRIEND", false, false, false, false, false, true, true, true);
        }

        if (friendship.isPending()) {
            if (friendship.getSenderId().equals(currentUserId)) {
                return new RelationState("OUTGOING_REQUEST", false, false, true, false, false, true, false, false);
            }
            return new RelationState("INCOMING_REQUEST", false, false, false, true, true, true, false, false);
        }

        return new RelationState("NONE", false, true, false, false, false, true, false, false);
    }

    private record RelationState(
            String relationStatus,
            boolean isSelf,
            boolean canAddFriend,
            boolean canCancelRequest,
            boolean canAcceptRequest,
            boolean canRejectRequest,
            boolean canMessage,
            boolean canCall,
            boolean canUnfriend
    ) {
    }
}