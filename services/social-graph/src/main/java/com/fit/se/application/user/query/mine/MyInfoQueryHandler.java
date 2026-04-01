package com.fit.se.application.user.query.mine;

import com.fit.se.domain.user.UserRepository;
import com.fit.se.domain.user.aggregate.User;
import com.fit.se.domain.user.valueobject.RelationPrivacyOverride;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyInfoQueryHandler {

    private final UserRepository userRepository;

    public MyInfoResult execute(MyInfoQuery query) {
        if (query == null || query.current() == null || query.current().isBlank()) {
            throw new IllegalArgumentException("Current user id must not be blank");
        }

        User user = userRepository.findById(query.current())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return toResult(user);
    }

    private MyInfoResult toResult(User user) {
        return MyInfoResult.builder()
                .id(user.getId())
                .userName(user.getUserName())

                .bio(user.getProfile().getBio())
                .gender(user.getProfile().getGender())
                .dob(user.getProfile().getDob())

                .avatar(user.getAppearance().getAvatar())
                .background(user.getAppearance().getBackground())
                .theme(user.getAppearance().getTheme())

                .birthdayVisibility(user.getPrivacy().getBirthdayPrivacy().getVisibility().name())
                .birthdayNotifyFriends(user.getPrivacy().getBirthdayPrivacy().isNotifyFriends())

                .relationPrivacyOverrides(toOverrideItems(user.getPrivacy().getRelationOverrides()))

                .qr(toQrItem(user))
                .build();
    }

    private MyInfoResult.QrItem toQrItem(User user) {
        if (user.getQrToken() == null) {
            return null;
        }

        return MyInfoResult.QrItem.builder()
                .token(user.getQrToken().getToken())
                .title(user.getQrToken().getTitle())
                .description(user.getQrToken().getDescription())
                .color(user.getQrToken().getColor())
                .build();
    }

    private Set<MyInfoResult.RelationPrivacyOverrideItem> toOverrideItems(
            Set<RelationPrivacyOverride> overrides
    ) {
        if (overrides == null || overrides.isEmpty()) {
            return Set.of();
        }

        return overrides.stream()
                .map(this::toOverrideItem)
                .collect(Collectors.toSet());
    }

    private MyInfoResult.RelationPrivacyOverrideItem toOverrideItem(RelationPrivacyOverride override) {
        return MyInfoResult.RelationPrivacyOverrideItem.builder()
                .targetUserId(override.getTargetUserId())
                .key(override.getKey().name())
                .decision(override.getDecision().name())
                .build();
    }
}