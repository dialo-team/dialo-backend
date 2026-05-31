package com.fit.se.user.application.command.bio;

import com.fit.se.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateBioCommandHandler {

    private final UserRepository userRepository;

    public void execute(UpdateBioCommand cmd) {
        var user = userRepository.findById(cmd.current())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.updateBio(cmd.bio());
        userRepository.save(user);
    }
}