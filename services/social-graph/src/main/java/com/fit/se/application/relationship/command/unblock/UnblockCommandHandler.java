package com.fit.se.application.relationship.command.unblock;

import com.fit.se.domain.relationship.RelationshipRepository;
import com.fit.se.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UnblockCommandHandler {
    private final UserRepository userRepo;
    private final RelationshipRepository relationshipRepo;

    public void execute(String from, String to) {
        relationshipRepo.unblock(from, to);
    }
}
