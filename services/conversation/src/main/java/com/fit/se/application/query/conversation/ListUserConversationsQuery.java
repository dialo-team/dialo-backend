package com.fit.se.application.query.conversation;

import com.fit.se.application.common.query.Query;

public record ListUserConversationsQuery(Long actorUserId) implements Query {
}
