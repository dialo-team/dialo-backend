package com.fit.se.application.user.command.qr;

import com.fit.se.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomizeQrCommandHandler {

    private final UserRepository userRepository;

    public void execute(CustomizeQrCommand cmd) {
        var user = userRepository.findById(cmd.current())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.customizeQr(
                cmd.title(),
                cmd.description(),
                cmd.color()
        );

        userRepository.save(user);
    }
}