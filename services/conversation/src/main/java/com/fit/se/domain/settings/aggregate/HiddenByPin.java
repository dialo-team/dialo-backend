package com.fit.se.domain.settings.aggregate;

public record HiddenByPin(boolean value) {
    public static HiddenByPin enabled() {
        return new HiddenByPin(true);
    }

    public static HiddenByPin disabled() {
        return new HiddenByPin(false);
    }
}
