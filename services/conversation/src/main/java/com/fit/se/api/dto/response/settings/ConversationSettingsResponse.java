package com.fit.se.api.dto.response.settings;

public record ConversationSettingsResponse(
        String conversationId,
        MemberSettingsResponse memberSettings
) {
}
