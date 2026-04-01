package com.fit.se.domain.user.aggregate;

import com.fit.se.domain.user.valueobject.Appearance;
import com.fit.se.domain.user.valueobject.Privacy;
import com.fit.se.domain.user.valueobject.Profile;
import com.fit.se.domain.user.valueobject.QrToken;
import lombok.Getter;

@Getter
public class User {
    private final String id;
    private String userName;
    private String phone;
    private QrToken qrToken;
    private Profile profile;
    private Appearance appearance;
    private Privacy privacy;

    private User(
            String id,
            String userName,
            String phone,
            QrToken qrToken,
            Profile profile,
            Appearance appearance,
            Privacy privacy
    ) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("User id must not be blank");
        }
        if (userName == null || userName.isBlank()) {
            throw new IllegalArgumentException("Username must not be blank");
        }
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("Phone must not be blank");
        }
        if (qrToken == null) {
            throw new IllegalArgumentException("QrToken must not be null");
        }
        if (profile == null) {
            throw new IllegalArgumentException("Profile must not be null");
        }
        if (appearance == null) {
            throw new IllegalArgumentException("Appearance must not be null");
        }
        if (privacy == null) {
            throw new IllegalArgumentException("Privacy must not be null");
        }

        this.id = id;
        this.userName = userName;
        this.phone = phone;
        this.qrToken = qrToken;
        this.profile = profile;
        this.appearance = appearance;
        this.privacy = privacy;
    }

    public static User create(
            String id,
            String userName,
            String phone,
            QrToken qrToken,
            Profile profile,
            Appearance appearance,
            Privacy privacy
    ) {
        return new User(id, userName, phone, qrToken, profile, appearance, privacy);
    }

    public static User createDefault(String id, String phone, String qrTokenValue) {
        return new User(
                id,
                "user_" + id,
                phone,
                QrToken.defaultOf(qrTokenValue),
                Profile.defaultProfile(),
                Appearance.defaultAppearance(),
                Privacy.defaultPrivacy()
        );
    }

    public static User reconstitute(
            String id,
            String userName,
            String phone,
            QrToken qrToken,
            Profile profile,
            Appearance appearance,
            Privacy privacy
    ) {
        return new User(id, userName, phone, qrToken, profile, appearance, privacy);
    }

    public void changeUserName(String userName) {
        if (userName == null || userName.isBlank()) {
            throw new IllegalArgumentException("Username must not be blank");
        }
        this.userName = userName;
    }

    public void syncPhone(String phone) {
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("Phone must not be blank");
        }
        this.phone = phone;
    }

    public void updateProfile(Profile profile) {
        if (profile == null) {
            throw new IllegalArgumentException("Profile must not be null");
        }
        this.profile = profile;
    }

    public void updateAppearance(Appearance appearance) {
        if (appearance == null) {
            throw new IllegalArgumentException("Appearance must not be null");
        }
        this.appearance = appearance;
    }

    public void updatePrivacy(Privacy privacy) {
        if (privacy == null) {
            throw new IllegalArgumentException("Privacy must not be null");
        }
        this.privacy = privacy;
    }

    public void updateQrToken(QrToken qrToken) {
        if (qrToken == null) {
            throw new IllegalArgumentException("QrToken must not be null");
        }
        this.qrToken = qrToken;
    }

    public void customizeQr(String title, String description, String color) {
        this.qrToken = this.qrToken.customize(title, description, color);
    }

    public void rotateQrToken(String newToken) {
        this.qrToken = this.qrToken.rotateToken(newToken);
    }
}