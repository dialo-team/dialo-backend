package com.fit.se.domain.settings.repository;

import com.fit.se.domain.common.valueobject.UserId;
import com.fit.se.domain.conversation.valueobject.ConversationId;
import com.fit.se.domain.settings.aggregate.ConversationMemberSettings;

import java.util.Optional;

public interface ConversationMemberSettingsRepository {
    Optional<ConversationMemberSettings> findByConversationIdAndUserId(ConversationId conversationId, UserId userId);

    ConversationMemberSettings save(ConversationMemberSettings settings);
}
