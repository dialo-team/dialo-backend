package com.fit.se.user.application.command.privacy;

import com.fit.se.user.domain.UserRepository;
import com.fit.se.user.domain.valueobject.Privacy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class RemovePrivacyOverrideCommandHandler {

    private final UserRepository userRepository;

    public void execute(RemovePrivacyOverrideCommand cmd) {
        var user = userRepository.findById(cmd.current())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        var oldPrivacy = user.getPrivacy();
        var overrides = new HashSet<>(oldPrivacy.getRelationOverrides());

        overrides.removeIf(it ->
                it.getTargetUserId().equals(cmd.targetId())
                        && it.getKey().name().equals(cmd.key())
        );

        var newPrivacy = new Privacy(
                oldPrivacy.getBirthdayPrivacy(),
                overrides
        );

        user.updatePrivacy(newPrivacy);
        userRepository.save(user);
    }
}