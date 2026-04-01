package com.fit.se.application.user.command.appearance;

import com.fit.se.domain.user.UserRepository;
import com.fit.se.domain.user.valueobject.Appearance;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateAppearanceCommandHandler {

    private final UserRepository userRepository;

    public void execute(UpdateAppearanceCommand cmd) {
        var user = userRepository.findById(cmd.current())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.updateAppearance(new Appearance(
                cmd.avatar(),
                cmd.background(),
                cmd.theme()
        ));

        userRepository.save(user);
    }
}