package com.fit.se.user.application.command.privacy;

import lombok.Builder;

@Builder
public record UpdatePrivacyCommand(
        String current,
        String birthdayVisibility,
        boolean birthdayNotifyFriends
) {
}