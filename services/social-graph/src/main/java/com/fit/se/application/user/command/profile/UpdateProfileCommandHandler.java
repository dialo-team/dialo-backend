package com.fit.se.application.user.command.profile;

import com.fit.se.domain.user.UserRepository;
import com.fit.se.domain.user.valueobject.Profile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateProfileCommandHandler {

    private final UserRepository userRepository;

    public void execute(UpdateProfileCommand cmd) {
        var user = userRepository.findById(cmd.current())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.updateProfile(new Profile(
                cmd.bio(),
                cmd.gender(),
                cmd.dob()
        ));

        userRepository.save(user);
    }
}