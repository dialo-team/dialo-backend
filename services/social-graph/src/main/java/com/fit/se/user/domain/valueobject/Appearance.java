package com.fit.se.user.domain.valueobject;

import lombok.Getter;

import java.util.Objects;

@Getter
public class Appearance {
    private final String avatar;
    private final String background;
    private final String theme;

    public Appearance(String avatar, String background, String theme) {
        this.avatar = avatar;
        this.background = background;
        this.theme = theme;
    }

    public static Appearance defaultAppearance() {
        return new Appearance(null, null, "LIGHT");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Appearance that)) return false;
        return Objects.equals(avatar, that.avatar)
                && Objects.equals(background, that.background)
                && Objects.equals(theme, that.theme);
    }

    @Override
    public int hashCode() {
        return Objects.hash(avatar, background, theme);
    }
}