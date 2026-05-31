package com.fit.se.user.domain.aggregate;

import com.fit.se.user.domain.valueobject.Appearance;
import com.fit.se.user.domain.valueobject.Privacy;
import com.fit.se.user.domain.valueobject.Profile;
import com.fit.se.user.domain.valueobject.QrToken;
import lombok.Getter;

@Getter
public class User {
    private final String id;
    private final Long version;

    private String userName;
    private String phone;
    private QrToken qrToken;
    private Profile profile;
    private Appearance appearance;
    private Privacy privacy;

    private User(
            String id,
            Long version,
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
        this.version = version;
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
        return new User(id, null, userName, phone, qrToken, profile, appearance, privacy);
    }

    public static User createDefault(String id, String phone, String qrTokenValue) {
        return new User(
                id,
                null,
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
            Long version,
            String userName,
            String phone,
            QrToken qrToken,
            Profile profile,
            Appearance appearance,
            Privacy privacy
    ) {
        return new User(id, version, userName, phone, qrToken, profile, appearance, privacy);
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

    public void updateAvatar(String avatarUrl) {
        this.appearance = new Appearance(
                avatarUrl,
                this.appearance.getBackground(),
                this.appearance.getTheme()
        );
    }

    public void updateBackground(String backgroundUrl) {
        this.appearance = new Appearance(
                this.appearance.getAvatar(),
                backgroundUrl,
                this.appearance.getTheme()
        );
    }

    public void updateBio(String bio) {
        this.profile = new Profile(
                bio,
                this.profile.getGender(),
                this.profile.getDob()
        );
    }

    public void updateBasicInfo(String userName, java.time.LocalDate dob, String gender) {
        if (userName == null || userName.isBlank()) {
            throw new IllegalArgumentException("Username must not be blank");
        }

        this.userName = userName;
        this.profile = new Profile(
                this.profile.getBio(),
                gender,
                dob
        );
    }
}