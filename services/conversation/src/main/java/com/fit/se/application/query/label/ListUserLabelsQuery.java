package com.fit.se.application.query.label;

import com.fit.se.application.common.query.Query;

public record ListUserLabelsQuery(Long actorUserId) implements Query {
}
