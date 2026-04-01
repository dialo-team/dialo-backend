package com.fit.se.application.user.command.privacy;

import com.fit.se.domain.user.UserRepository;
import com.fit.se.domain.user.valueobject.BirthdayPrivacy;
import com.fit.se.domain.user.valueobject.BirthdayVisibility;
import com.fit.se.domain.user.valueobject.Privacy;
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