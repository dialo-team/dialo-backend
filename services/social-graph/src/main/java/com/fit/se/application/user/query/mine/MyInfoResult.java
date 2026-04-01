package com.fit.se.application.user.query.mine;

import lombok.Builder;

import java.time.LocalDate;
import java.util.Set;

@Builder
public record MyInfoResult(
        String id,
        String userName,
        String bio,
        String gender,
        LocalDate dob,
        String avatar,
        String background,
        String theme,
        String birthdayVisibility,
        boolean birthdayNotifyFriends,
        Set<RelationPrivacyOverrideItem> relationPrivacyOverrides,
        QrItem qr
) {
    @Builder
    public record QrItem(
            String token,
            String title,
            String description,
            String color
    ) {
    }

    @Builder
    public record RelationPrivacyOverrideItem(
            String targetUserId,
            String key,
            String decision
    ) {
    }
}