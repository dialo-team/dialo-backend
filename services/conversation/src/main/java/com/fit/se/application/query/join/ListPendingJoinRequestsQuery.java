package com.yourcompany.conversationservice.application.query.join;

import com.yourcompany.conversationservice.application.common.query.Query;

public record ListPendingJoinRequestsQuery(String conversationId, Long actorUserId) implements Query {
}
