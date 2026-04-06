package com.fit.se.api.mapper.settings;

import com.fit.se.api.dto.response.settings.ConversationSettingsResponse;
import com.fit.se.api.dto.response.settings.MemberSettingsResponse;
import com.fit.se.application.result.settings.ConversationSettingsResult;
import com.fit.se.application.result.settings.MemberSettingsResult;
import org.springframework.stereotype.Component;

@Component
public class SettingsApiMapper {

    public MemberSettingsResponse toMemberSettingsResponse(MemberSettingsResult result) {
        return new MemberSettingsResponse(result.pinned(), result.muted(), result.hidden(), result.alias());
    }

    public ConversationSettingsResponse toConversationSettingsResponse(ConversationSettingsResult result) {
        return new ConversationSettingsResponse(result.conversationId(), toMemberSettingsResponse(result.memberSettings()));
    }
}
