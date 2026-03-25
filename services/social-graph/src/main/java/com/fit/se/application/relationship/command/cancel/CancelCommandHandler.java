package com.fit.se.application.relationship.command.cancel;

import com.fit.se.domain.relationship.RelationshipRepository;
import com.fit.se.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CancelCommandHandler {
    private final UserRepository userRepo;
    private final RelationshipRepository relationshipRepo;

    public void execute(String from, String to) {
        if (!relationshipRepo.hasPendingRequest(from, to)
                && !relationshipRepo.hasPendingRequest(to, from)) {
            throw new IllegalStateException("No pending request");
        }

        relationshipRepo.cancelFriendRequest(from, to);
    }
}
