package com.fit.se.api.dto.response.bootstrap;

public record BootstrapStatusResponse(
        Long userId,
        boolean selfConversationCreated,
        boolean systemConversationCreated,
        boolean defaultLabelsCreated
) {
}
