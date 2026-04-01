package com.fit.se.domain.user.valueobject;

public class QrStyle {
    private final String title;
    private final String description;
    private final String color;

    public QrStyle(String title, String description, String style) {
        this.title = title;
        this.description = description;
        this.color = style;
    }
}
