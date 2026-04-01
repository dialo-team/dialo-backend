package com.fit.se.application.user.command.privacy;

import lombok.Builder;

@Builder
public record UpdatePrivacyCommand(
        String current,
        String birthdayVisibility,
        boolean birthdayNotifyFriends
) {
}