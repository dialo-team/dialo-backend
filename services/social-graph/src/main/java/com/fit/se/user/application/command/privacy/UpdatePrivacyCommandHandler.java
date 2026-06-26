package com.fit.se.user.application.command.privacy;

import com.fit.se.user.domain.UserRepository;
import com.fit.se.user.domain.valueobject.BirthdayPrivacy;
import com.fit.se.user.domain.valueobject.BirthdayVisibility;
import com.fit.se.user.domain.valueobject.Privacy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdatePrivacyCommandHandler {

    private final UserRepository userRepository;

    public void execute(UpdatePrivacyCommand cmd) {
        var user = userRepository.findById(cmd.current())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Privacy currentPrivacy = user.getPrivacy();

        Privacy newPrivacy = new Privacy(
                new BirthdayPrivacy(
                        BirthdayVisibility.valueOf(cmd.birthdayVisibility()),
                        cmd.birthdayNotifyFriends()
                ),
                currentPrivacy.getRelationOverrides()
        );

        user.updatePrivacy(newPrivacy);
        userRepository.save(user);
    }
}