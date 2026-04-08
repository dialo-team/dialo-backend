package com.fit.se.application.query.membership;

import com.fit.se.application.common.query.Query;

public record GetMemberRoleQuery(String conversationId, Long actorUserId, Long targetUserId) implements Query {
}
