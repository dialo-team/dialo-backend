package com.yourcompany.conversationservice.application.query.conversation;

import com.yourcompany.conversationservice.application.common.query.Query;

public record GetConversationByDirectPairQuery(Long actorUserId, Long otherUserId) implements Query {
}
