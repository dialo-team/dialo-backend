package com.fit.se.domain.label.model;

public record UserConversationLabel(
        Long id,
        Long userId,
        String name,
        String color,
        LabelType type,
        boolean deletable
) {
}
