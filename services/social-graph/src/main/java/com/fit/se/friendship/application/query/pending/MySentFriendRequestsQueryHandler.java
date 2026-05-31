package com.fit.se.friendship.application.query.pending;

import com.fit.se.friendship.domain.FriendshipRepository;
import com.fit.se.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MySentFriendRequestsQueryHandler {

    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;

    public List<PendingFriendRequestView> execute(MyPendingFriendRequestsQuery query) {
        return friendshipRepository.findPendingSentOf(query.current())
                .stream()
                .map(it -> {
                    var sender = userRepository.findById(it.getSenderId())
                            .orElseThrow(() -> new IllegalArgumentException("Sender not found: " + it.getSenderId()));
                    var receiver = userRepository.findById(it.getReceiverId())
                            .orElseThrow(() -> new IllegalArgumentException("Receiver not found: " + it.getReceiverId()));
                    return PendingFriendRequestView.builder()
                            .friendshipId(it.getId())
                            .senderId(it.getSenderId())
                            .senderUserName(sender.getUserName())
                            .senderAvatar(sender.getAppearance().getAvatar())
                            .receiverId(it.getReceiverId())
                            .receiverUserName(receiver.getUserName())
                            .receiverAvatar(receiver.getAppearance().getAvatar())
                            .reason(it.getReason())
                            .requestedAt(it.getRequestedAt())
                            .build();
                })
                .toList();
    }
}