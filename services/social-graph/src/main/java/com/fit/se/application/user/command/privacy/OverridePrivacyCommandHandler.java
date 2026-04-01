package com.fit.se.application.user.command.privacy;

import com.fit.se.domain.user.UserRepository;
import com.fit.se.domain.user.valueobject.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class OverridePrivacyCommandHandler {

    private final UserRepository userRepository;

    public void execute(OverridePrivacyCommand cmd) {
        var user = userRepository.findById(cmd.current())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        var oldPrivacy = user.getPrivacy();
        var overrides = new HashSet<>(oldPrivacy.getRelationOverrides());

        overrides.removeIf(it ->
                it.getTargetUserId().equals(cmd.targetId())
                        && it.getKey().name().equals(cmd.key())
        );

        overrides.add(new RelationPrivacyOverride(
                cmd.targetId(),
                RelationPrivacyKey.valueOf(cmd.key()),
                RelationPrivacyDecision.valueOf(cmd.decision())
        ));

        var newPrivacy = new Privacy(
                oldPrivacy.getBirthdayPrivacy(),
                overrides
        );

        user.updatePrivacy(newPrivacy);
        userRepository.save(user);
    }
}