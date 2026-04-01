package com.fit.se.application.user.command.sync;

import com.fit.se.domain.user.aggregate.User;
import com.fit.se.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SyncUserCommandHandler {
    private final UserRepository userRepo;

    public void execute(SyncUserCommand cmd) {
        if (userRepo.existsById(cmd.userId())) {
            throw new IllegalArgumentException("User already exists: " + cmd.userId());
        }

        User newUser = User.createDefault(cmd.userId(), cmd.phone(), UUID.randomUUID().toString());
        userRepo.save(newUser);
    }
}
