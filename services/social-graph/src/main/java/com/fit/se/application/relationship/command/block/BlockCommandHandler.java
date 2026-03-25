package com.fit.se.application.relationship.command.block;

import com.fit.se.domain.relationship.RelationshipRepository;
import com.fit.se.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BlockCommandHandler {
    private final UserRepository userRepo;
    private final RelationshipRepository relationshipRepo;

    public void execute(String from, String to) {
        relationshipRepo.block(from, to);
        relationshipRepo.removeFriendship(from, to);
        relationshipRepo.removeRequest(from, to);
    }
}
