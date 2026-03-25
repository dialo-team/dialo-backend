package com.fit.se.application.user.command.sync;

import com.fit.se.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SyncCommandHandler {
    private final UserRepository userRepo;

    public void execute() {
        userRepo.create();
    }
}
