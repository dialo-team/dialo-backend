package com.fit.se.application.query.join;

import com.fit.se.application.common.query.Query;

public record GetJoinTokenInfoQuery(String conversationId, Long actorUserId) implements Query {
}
