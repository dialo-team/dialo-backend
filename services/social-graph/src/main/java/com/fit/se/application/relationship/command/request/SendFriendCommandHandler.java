package com.fit.se.application.relationship.command.request;

import com.fit.se.domain.relationship.RelationshipRepository;
import com.fit.se.domain.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SendFriendCommandHandler {
    private final UserRepository userRepo;
    private final RelationshipRepository relationshipRepo;

    @Transactional
    public void execute(String from, String to) {
        if (from.equals(to)) {
            throw new IllegalArgumentException("Cannot friend yourself");
        }

        if (relationshipRepo.isBlocked(from, to)) {
            throw new IllegalStateException("User is blocked");
        }

        if (relationshipRepo.isFriend(from, to)) {
            throw new IllegalStateException("Already friends");
        }

        if (relationshipRepo.hasPendingRequest(from, to)) {
            return; // idempotent
        }

        relationshipRepo.createFriendRequest(from, to);
    }
}
