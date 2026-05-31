package com.fit.se.user.domain.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class QrToken {
    private final String token;
    private final String title;
    private final String description;
    private final String color;

    public QrToken(String token, String title, String description, String color) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("QR token must not be blank");
        }
        this.token = token;
        this.title = title;
        this.description = description;
        this.color = color;
    }

    public QrToken customize(String title, String description, String color) {
        return new QrToken(this.token, title, description, color);
    }

    public QrToken rotateToken(String newToken) {
        if (newToken == null || newToken.isBlank()) {
            throw new IllegalArgumentException("QR token must not be blank");
        }
        return new QrToken(newToken, this.title, this.description, this.color);
    }

    public static QrToken defaultOf(String token) {
        return new QrToken(token, null, null, null);
    }
}