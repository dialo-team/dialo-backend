package com.fit.se.application.user.command.qr;

import com.fit.se.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RotateQrCommandHandler {

    private final UserRepository userRepository;

    public void execute(RotateQrCommand cmd) {
        var user = userRepository.findById(cmd.current())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String newToken = UUID.randomUUID().toString();

        user.rotateQrToken(newToken);
        userRepository.save(user);
    }
}