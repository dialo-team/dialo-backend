package com.yourcompany.conversationservice.application.query.conversation;

import com.yourcompany.conversationservice.application.common.query.Query;

public record GetConversationDetailQuery(String conversationId, Long actorUserId) implements Query {
}
