package com.fit.se.application.relationship.command.accept;

import com.fit.se.domain.relationship.RelationshipRepository;
import com.fit.se.domain.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AcceptCommandHandler {
    private final UserRepository userRepo;
    private final RelationshipRepository relationshipRepo;

    @Transactional
    public void execute(String from, String to) {
        if (!relationshipRepo.hasPendingRequest(from, to)) {
            throw new IllegalStateException("No pending request");
        }

        relationshipRepo.acceptFriend(from, to);
    }
}
