package com.yourcompany.conversationservice.application.query.membership;

import com.yourcompany.conversationservice.application.common.query.Query;

public record GetMemberRoleQuery(String conversationId, Long actorUserId, Long targetUserId) implements Query {
}
