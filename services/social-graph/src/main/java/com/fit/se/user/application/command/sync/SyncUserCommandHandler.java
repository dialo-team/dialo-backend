package com.fit.se.user.application.command.sync;

import com.fit.se.user.domain.UserRepository;
import com.fit.se.user.domain.aggregate.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SyncUserCommandHandler {
    private final UserRepository userRepo;

    public void execute(SyncUserCommand cmd) {
        if (userRepo.existsById(cmd.userId())) {
            return;
        }

        User newUser = User.createDefault(cmd.userId(), cmd.phone(), UUID.randomUUID().toString());
        userRepo.save(newUser);
    }
}