package com.fit.se.application.query.conversation;

import com.fit.se.application.common.query.Query;

public record GetConversationDetailQuery(String conversationId, Long actorUserId) implements Query {
}
