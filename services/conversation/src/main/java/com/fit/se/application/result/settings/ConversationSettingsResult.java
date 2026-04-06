package com.yourcompany.conversationservice.application.result.settings;

import com.yourcompany.conversationservice.application.common.query.QueryResult;

public record ConversationSettingsResult(String conversationId, boolean pinned, boolean muted, boolean hidden, String alias, String assignedLabelId) implements QueryResult {
}
