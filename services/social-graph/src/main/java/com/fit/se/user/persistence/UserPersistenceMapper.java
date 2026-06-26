package com.fit.se.user.persistence;

import com.fit.se.user.domain.aggregate.User;
import com.fit.se.user.domain.valueobject.Appearance;
import com.fit.se.user.domain.valueobject.BirthdayPrivacy;
import com.fit.se.user.domain.valueobject.BirthdayVisibility;
import com.fit.se.user.domain.valueobject.Privacy;
import com.fit.se.user.domain.valueobject.Profile;
import com.fit.se.user.domain.valueobject.QrToken;
import com.fit.se.user.domain.valueobject.RelationPrivacyDecision;
import com.fit.se.user.domain.valueobject.RelationPrivacyKey;
import com.fit.se.user.domain.valueobject.RelationPrivacyOverride;
import com.fit.se.user.persistence.node.UserNode;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public final class UserPersistenceMapper {

    private UserPersistenceMapper() {
    }

    public static UserNode toNode(User user) {
        UserNode node = new UserNode();
        node.setId(user.getId());
        node.setVersion(user.getVersion());
        node.setUserName(user.getUserName());
        node.setPhone(user.getPhone());

        node.setBio(user.getProfile().getBio());
        node.setGender(user.getProfile().getGender());
        node.setDob(user.getProfile().getDob());

        node.setAvatar(user.getAppearance().getAvatar());
        node.setBackground(user.getAppearance().getBackground());
        node.setTheme(user.getAppearance().getTheme());

        node.setBirthdayVisibility(user.getPrivacy().birthdayPrivacy().getVisibility().name());
        node.setBirthdayNotifyFriends(user.getPrivacy().birthdayPrivacy().isNotifyFriends());

        Map<String, String> privacyMap = new HashMap<>();
        for (RelationPrivacyOverride it : user.getPrivacy().relationOverrides()) {
            String mapKey = it.getTargetUserId() + "__" + it.getKey().name();
            privacyMap.put(mapKey, it.getDecision().name());
        }
        node.setRelationPrivacyOverrides(privacyMap);

        node.setQrTokenValue(user.getQrToken().getToken());
        node.setQrTitle(user.getQrToken().getTitle());
        node.setQrDescription(user.getQrToken().getDescription());
        node.setQrColor(user.getQrToken().getColor());

        return node;
    }

    public static User toDomain(UserNode node) {
        Profile profile = new Profile(
                node.getBio(),
                node.getGender(),
                node.getDob()
        );

        Appearance appearance = new Appearance(
                node.getAvatar(),
                node.getBackground(),
                node.getTheme()
        );

        Map<String, String> rawOverrides = node.getRelationPrivacyOverrides();
        if (rawOverrides == null) {
            rawOverrides = Map.of();
        }

        Set<RelationPrivacyOverride> overrides = rawOverrides.entrySet()
                .stream()
                .map(UserPersistenceMapper::toRelationOverride)
                .collect(Collectors.toSet());

        BirthdayVisibility visibility = node.getBirthdayVisibility() == null
                ? BirthdayVisibility.HIDDEN
                : BirthdayVisibility.valueOf(node.getBirthdayVisibility());

        Privacy privacy = new Privacy(
                new BirthdayPrivacy(
                        visibility,
                        node.isBirthdayNotifyFriends()
                ),
                overrides
        );

        String qrValue = node.getQrTokenValue();
        if (qrValue == null || qrValue.isBlank()) {
            throw new IllegalStateException("UserNode is missing qrTokenValue: " + node.getId());
        }

        QrToken qrToken = new QrToken(
                qrValue,
                node.getQrTitle(),
                node.getQrDescription(),
                node.getQrColor()
        );

        return User.reconstitute(
                node.getId(),
                node.getVersion(),
                node.getUserName(),
                node.getPhone(),
                qrToken,
                profile,
                appearance,
                privacy
        );
    }

    private static RelationPrivacyOverride toRelationOverride(Map.Entry<String, String> entry) {
        String[] parts = entry.getKey().split("__", 2);
        return new RelationPrivacyOverride(
                parts[0],
                RelationPrivacyKey.valueOf(parts[1]),
                RelationPrivacyDecision.valueOf(entry.getValue())
        );
    }
}
