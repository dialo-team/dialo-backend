package com.fit.se.application.query.join;

import com.fit.se.application.common.query.Query;

public record GetJoinRequestDetailQuery(String joinRequestId, Long actorUserId) implements Query {
}
