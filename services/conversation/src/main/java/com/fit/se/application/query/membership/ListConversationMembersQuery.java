package com.yourcompany.conversationservice.application.query.membership;

import com.yourcompany.conversationservice.application.common.query.Query;

public record ListConversationMembersQuery(String conversationId, Long actorUserId) implements Query {
}
