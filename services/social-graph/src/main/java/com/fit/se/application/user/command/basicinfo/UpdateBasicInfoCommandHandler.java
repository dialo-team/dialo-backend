package com.fit.se.application.user.command.basicinfo;

import com.fit.se.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateBasicInfoCommandHandler {

    private final UserRepository userRepository;

    public void execute(UpdateBasicInfoCommand cmd) {
        var user = userRepository.findById(cmd.current())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.updateBasicInfo(
                cmd.userName(),
                cmd.dob(),
                cmd.gender()
        );

        userRepository.save(user);
    }
}