package com.yourcompany.conversationservice.application.query.join;

import com.yourcompany.conversationservice.application.common.query.Query;

public record GetJoinRequestDetailQuery(String joinRequestId, Long actorUserId) implements Query {
}
