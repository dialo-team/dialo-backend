package com.fit.se.application.relationship.command.unfriend;

import com.fit.se.domain.relationship.RelationshipRepository;
import com.fit.se.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UnfriendCommandHandler {
    private final UserRepository userRepo;
    private final RelationshipRepository relationshipRepo;

    public void execute(String from, String to) {
        if (!relationshipRepo.isFriend(from, to)) {
            throw new IllegalStateException("Not friends");
        }

        relationshipRepo.unfriend(from, to);
    }
}
