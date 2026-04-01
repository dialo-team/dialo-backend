package com.fit.se.api.dto.request;

public record UpdatePrivacyRequest(
        String birthdayVisibility,
        boolean birthdayNotifyFriends
) {
}
