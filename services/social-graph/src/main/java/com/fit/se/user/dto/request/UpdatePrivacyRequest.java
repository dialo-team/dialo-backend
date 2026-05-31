package com.fit.se.user.dto.request;

public record UpdatePrivacyRequest(
        String birthdayVisibility,
        boolean birthdayNotifyFriends
) {
}
