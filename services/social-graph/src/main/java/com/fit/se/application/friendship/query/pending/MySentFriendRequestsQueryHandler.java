package com.fit.se.application.friendship.query.pending;

import com.fit.se.domain.friendship.FriendshipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MySentFriendRequestsQueryHandler {

    private final FriendshipRepository friendshipRepository;

    public List<PendingFriendRequestView> execute(MyPendingFriendRequestsQuery query) {
        return friendshipRepository.findPendingSentOf(query.current())
                .stream()
                .map(it -> PendingFriendRequestView.builder()
                        .friendshipId(it.getId())
                        .senderId(it.getSenderId())
                        .receiverId(it.getReceiverId())
                        .requestedAt(it.getRequestedAt())
                        .build())
                .toList();
    }
}