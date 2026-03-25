package com.fit.se.api.controller;

import com.fit.se.application.user.command.sync.SyncCommandHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final SyncCommandHandler syncCommandHandler;

    @PostMapping
    public void syncUser() {
        syncCommandHandler.execute();
    }
}
