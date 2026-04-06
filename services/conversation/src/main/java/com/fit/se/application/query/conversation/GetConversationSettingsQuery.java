package com.yourcompany.conversationservice.application.query.conversation;

import com.yourcompany.conversationservice.application.common.query.Query;

public record GetConversationSettingsQuery(String conversationId, Long actorUserId) implements Query {
}
