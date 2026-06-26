package com.fit.se.user.application.command.qr;

import com.fit.se.user.domain.UserRepository;
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